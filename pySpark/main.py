import os
import sys
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import Pipeline
from pyspark.ml.feature import VectorAssembler, StringIndexer
from pyspark.ml.classification import RandomForestClassifier
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

# --- RĘCZNA KONFIGURACJA ŚCIEŻEK ---
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

spark_bin = os.path.join(os.environ['SPARK_HOME'], 'bin')
java_bin = os.path.join(os.environ['JAVA_HOME'], 'bin')
os.environ['PATH'] = spark_bin + os.pathsep + java_bin + os.pathsep + os.environ['PATH']


def start_spark():
    print("Inicjalizacja bezpiecznej sesji Spark...")
    try:
        spark = SparkSession.builder \
            .appName("WindSense-ML-Training") \
            .master("local[*]") \
            .config("spark.python.worker.reuse", "false") \
            .getOrCreate()
        print("!!! SUKCES: Sesja Spark utworzona !!!")
        return spark
    except Exception as e:
        print(f"\nBŁĄD KRYTYCZNY PRZY STARTOWANIU SPARKA:\n{e}")
        return None


def run_training(spark):
    print("\n--- ROZPOCZĘCIE PROCESU ML ---")
    try:
        # 1. Wczytanie danych
        path_to_csv = "turbine_fleet_final_v11.csv"
        if not os.path.exists(path_to_csv):
            print(f"BŁĄD: Nie znaleziono pliku {path_to_csv}!")
            return

        df = spark.read.csv(path_to_csv, header=True, inferSchema=True)
        print(f"Wczytano {df.count()} rekordów.")

        # 2. Pipeline przygotowania danych
        # Zamieniamy turbine_type (L, M, H) na indeksy liczbowe
        type_indexer = StringIndexer(inputCol="turbine_type", outputCol="type_index", handleInvalid="keep")

        # Wybieramy cechy fizyczne (bez timestamp i turbine_id)
        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]
        assembler = VectorAssembler(inputCols=feature_cols, outputCol="features")

        # 3. Model Random Forest
        rf = RandomForestClassifier(labelCol="label_id", featuresCol="features", numTrees=100, maxDepth=10, seed=42)

        # 4. Składanie Pipeline
        pipeline = Pipeline(stages=[type_indexer, assembler, rf])

        # 5. Podział danych
        train_data, test_data = df.randomSplit([0.8, 0.2], seed=42)

        print("Trenowanie modelu... (może to zająć chwilę)")
        model = pipeline.fit(train_data)

        # 6. Testowanie i Ewaluacja
        predictions = model.transform(test_data)

        evaluator = MulticlassClassificationEvaluator(labelCol="label_id", predictionCol="prediction", metricName="f1")
        f1_score = evaluator.evaluate(predictions)

        print(f"\nModel wytrenowany pomyślnie!")
        print(f"F1-Score na zbiorze testowym: {f1_score:.4f}")

        # Pokaż wyniki dla kilku przykładów (szczególnie tam gdzie label_id > 0)
        print("\nPrzykładowe predykcje (Prawda vs Model):")
        predictions.filter(predictions.label_id > 0).select("turbine_id", "label_id", "prediction").show(10)

        # 7. Zapisywanie modelu
        model_path = "wind_turbine_model_v1"
        model.write().overwrite().save(model_path)
        print(f"Model został zapisany w: {os.path.abspath(model_path)}")

    except Exception as e:
        print(f"BŁĄD PODCZAS TRENOWANIA:\n{e}")


if __name__ == "__main__":
    # Uruchomienie sesji
    session = start_spark()

    if session:
        # Uruchomienie treningu
        run_training(session)

        # Zamykanie sesji
        print("\nZamykanie sesji Spark.")
        session.stop()
    else:
        print("Nie udało się rozpocząć sesji. Sprawdź konfigurację środowiska.")