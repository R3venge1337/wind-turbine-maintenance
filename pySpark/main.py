import os
import sys
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import Pipeline
from pyspark.ml.feature import VectorAssembler, StringIndexer, StandardScaler
from pyspark.ml.classification import RandomForestClassifier
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

# --- KONFIGURACJA ŚCIEŻEK (Pozostawiona bez zmian) ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable
os.environ['SPARK_HOME'] = os.path.dirname(pyspark.__file__)

os.environ["PYSPARK_SUBMIT_ARGS"] = (
    '--conf "spark.driver.extraJavaOptions=-Djdk.security.allowGetSubjectCall=true '
    '--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED '
    '--add-opens=java.base/sun.security.action=ALL-UNNAMED" '
    'pyspark-shell'
)


def start_spark():
    print("Inicjalizacja lokalnej sesji Spark...")
    try:
        spark = SparkSession.builder \
            .appName("WindSense-ML-Training") \
            .master("local[*]") \
            .config("spark.driver.memory", "4g") \
            .config("spark.executor.memory", "4g") \
            .getOrCreate()
        print("!!! SUKCES: Sesja Spark utworzona !!!")
        return spark
    except Exception as e:
        print(f"BŁĄD: {e}")
        return None


def evaluate_metrics(predictions, dataset_name):
    """Pomocnicza funkcja do obliczania wszystkich metryk"""
    evaluator = MulticlassClassificationEvaluator(labelCol="label_id", predictionCol="prediction")

    acc = evaluator.evaluate(predictions, {evaluator.metricName: "accuracy"})
    f1 = evaluator.evaluate(predictions, {evaluator.metricName: "f1"})
    prec = evaluator.evaluate(predictions, {evaluator.metricName: "weightedPrecision"})
    rec = evaluator.evaluate(predictions, {evaluator.metricName: "weightedRecall"})

    print(f"\n--- Metryki dla: {dataset_name} ---")
    print(f"Accuracy:  {acc:.4f}")
    print(f"F1-Score:  {f1:.4f}")
    print(f"Precision: {prec:.4f}")
    print(f"Recall:    {rec:.4f}")


def run_training(spark):
    print("\n--- ROZPOCZĘCIE PROCESU ML ---")
    try:
        path_to_csv = "turbine_fleet_final.csv"
        if not os.path.exists(path_to_csv):
            print(f"BŁĄD: Nie znaleziono pliku {path_to_csv}!")
            return

        df = spark.read.csv(path_to_csv, header=True, inferSchema=True)
        print(f"Wczytano {df.count()} rekordów.")

        # 1. Pipeline przygotowania danych
        type_indexer = StringIndexer(inputCol="turbine_type", outputCol="type_index", handleInvalid="keep")

        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]
        assembler = VectorAssembler(inputCols=feature_cols, outputCol="unscaled_features")

        # DODANO: Standaryzacja cech
        scaler = StandardScaler(inputCol="unscaled_features", outputCol="features", withStd=True, withMean=False)

        # 2. Model Random Forest (zwiększona głębia zgodnie z Twoim planem)
        rf = RandomForestClassifier(labelCol="label_id", featuresCol="features", numTrees=100, maxDepth=15, seed=42)

        # 3. Składanie Pipeline (Indexer -> Assembler -> Scaler -> RF)
        pipeline = Pipeline(stages=[type_indexer, assembler, scaler, rf])

        # 4. Podział danych
        train_data, test_data = df.randomSplit([0.7, 0.3], seed=42)

        print("Trenowanie modelu... (to może potrwać kilka minut na lokalnym CPU)")
        model = pipeline.fit(train_data)

        # 5. Predykcje dla obu zbiorów (aby sprawdzić over-fitting)
        train_predictions = model.transform(train_data)
        test_predictions = model.transform(test_data)

        # 6. Pełna ewaluacja metryk
        evaluate_metrics(train_predictions, "ZBIÓR TRENINGOWY")
        evaluate_metrics(test_predictions, "ZBIÓR TESTOWY")

        # 7. Pokazanie błędów (tylko dla rekordów gdzie wystąpiła awaria)
        print("\nPrzykładowe predykcje na zbiorze testowym (tylko awarie):")
        test_predictions.filter(test_predictions.label_id > 0).select("turbine_id", "label_id", "prediction").show(10)

        # 8. Zapisywanie modelu
        model_path = "wind_turbine_model_v1"
        model.write().overwrite().save(model_path)
        print(f"\nModel zapisany: {os.path.abspath(model_path)}")

    except Exception as e:
        print(f"BŁĄD PODCZAS TRENOWANIA:\n{e}")


if __name__ == "__main__":
    session = start_spark()
    if session:
        run_training(session)
        print("\nZamykanie sesji Spark.")
        session.stop()