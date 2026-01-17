import os
import sys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import Pipeline
from pyspark.ml.feature import VectorAssembler, StringIndexer, StandardScaler
from pyspark.ml.classification import RandomForestClassifier
from pyspark.ml.tuning import CrossValidator, ParamGridBuilder
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

# --- 1. KONFIGURACJA ŚCIEŻEK ŚRODOWISKOWYCH ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable
os.environ['SPARK_HOME'] = os.path.dirname(pyspark.__file__)

# Flagi JVM naprawiające błędy NativeIO na Windows (Java 17)
jvm_flags = (
    "-Djdk.security.allowGetSubjectCall=true "
    "--add-opens=java.base/java.lang=ALL-UNNAMED "
    "--add-opens=java.base/java.util=ALL-UNNAMED "
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
)


def save_detailed_reports(cv_model, paramGrid):
    """Generuje tabelę CSV i heatmapę ze wszystkich kombinacji parametrów."""
    print("\n--- GENEROWANIE RAPORTU EKSPERYMENTÓW ---")

    metrics = cv_model.avgMetrics
    params_list = []

    for i, params in enumerate(paramGrid):
        # Wyciągnięcie nazw i wartości parametrów
        p_dict = {p.name: v for p, v in params.items()}
        p_dict['F1_Score'] = round(metrics[i], 4)
        params_list.append(p_dict)

    # Tworzenie i zapis tabeli wyników
    results_df = pd.DataFrame(params_list)
    results_df = results_df.sort_values(by='F1_Score', ascending=False)
    results_df.to_csv("hypertuning_results_full.csv", index=False)

    print(results_df.to_string(index=False))

    # Generowanie heatmapy wpływu parametrów
    plt.figure(figsize=(10, 6))
    pivot_table = results_df.pivot(index='maxDepth', columns='numTrees', values='F1_Score')
    sns.heatmap(pivot_table, annot=True, cmap="YlGnBu", fmt=".4f")
    plt.title("Analiza Grid Search: maxDepth vs numTrees (F1-Score)")
    plt.tight_layout()
    plt.savefig("hypertuning_heatmap.png")
    print("\nZapisano: hypertuning_results_full.csv oraz hypertuning_heatmap.png")


def run_full_optimization():
    # Inicjalizacja sesji
    spark = SparkSession.builder \
        .appName("WindSense-Final-Optimization") \
        .master("local[*]") \
        .config("spark.driver.memory", "8g") \
        .config("spark.driver.extraJavaOptions", jvm_flags) \
        .getOrCreate()

    try:
        # 1. Wczytanie danych
        print("Wczytywanie danych...")
        df = spark.read.csv("turbine_fleet_final.csv", header=True, inferSchema=True)
        train_data, test_data = df.randomSplit([0.7, 0.3], seed=42)

        # 2. Pipeline ML
        type_indexer = StringIndexer(inputCol="turbine_type", outputCol="type_index")
        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]
        assembler = VectorAssembler(inputCols=feature_cols, outputCol="unscaled_features")
        scaler = StandardScaler(inputCol="unscaled_features", outputCol="features")

        rf = RandomForestClassifier(labelCol="label_id", featuresCol="features", seed=42)

        pipeline = Pipeline(stages=[type_indexer, assembler, scaler, rf])

        # 3. ParamGrid (Twoja konfiguracja z Colaba)
        paramGrid = (ParamGridBuilder()
                     .addGrid(rf.numTrees, [20, 40, 60, 80, 100])
                     .addGrid(rf.maxDepth, [5, 10, 15])
                     .build())

        # 4. Cross-Validator
        evaluator = MulticlassClassificationEvaluator(labelCol="label_id", predictionCol="prediction", metricName="f1")

        cv = CrossValidator(
            estimator=pipeline,
            estimatorParamMaps=paramGrid,
            evaluator=evaluator,
            numFolds=3,
            parallelism=2  # Przyspiesza proces na procesorach wielordzeniowych
        )

        print(f"Rozpoczynam Hypertuning (15 kombinacji x 3-fold CV = 45 treningów)...")
        cv_model = cv.fit(train_data)

        # 5. Zapisywanie szczegółowych wyników do tabeli
        save_detailed_reports(cv_model, paramGrid)

        # 6. Ewaluacja najlepszego modelu
        best_model = cv_model.bestModel
        predictions = best_model.transform(test_data)

        final_f1 = evaluator.evaluate(predictions)
        final_acc = evaluator.evaluate(predictions, {evaluator.metricName: "accuracy"})

        print("\n" + "=" * 40)
        print("NAJLEPSZY MODEL ZNALEZIONY:")
        best_rf = best_model.stages[-1]
        print(f"- numTrees: {best_rf.getNumTrees}")
        print(f"- maxDepth: {best_rf.getOrDefault('maxDepth')}")
        print(f"- Final F1-Score: {final_f1:.4f}")
        print(f"- Final Accuracy: {final_acc:.4f}")
        print("=" * 40)

        # 7. Zapis modelu
        best_model.write().overwrite().save("wind_turbine_best_model_optimized")
        print("\nModel optymalny zapisany pomyślnie.")

    except Exception as e:
        print(f"BŁĄD: {e}")
    finally:
        spark.stop()


if __name__ == "__main__":
    run_full_optimization()