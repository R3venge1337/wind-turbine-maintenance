import os
import sys
import pandas as pd
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

# --- 1. KONFIGURACJA ŚRODOWISKA ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable
os.environ['SPARK_HOME'] = os.path.dirname(pyspark.__file__)

# Flagi naprawcze dla Java 17 na Windows
jvm_flags = (
    "-Djdk.security.allowGetSubjectCall=true "
    "--add-opens=java.base/java.lang=ALL-UNNAMED "
    "--add-opens=java.base/java.util=ALL-UNNAMED "
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
)


def run_comparison():
    # 2. INICJALIZACJA SESJI Z FLAGAMI
    spark = SparkSession.builder \
        .appName("WindSense-Final-Comparison") \
        .master("local[*]") \
        .config("spark.driver.memory", "4g") \
        .config("spark.driver.extraJavaOptions", jvm_flags) \
        .config("spark.executor.extraJavaOptions", jvm_flags) \
        .getOrCreate()

    try:
        # 3. WCZYTANIE DANYCH
        print("Wczytywanie danych...")
        df = spark.read.csv("turbine_fleet_final.csv", header=True, inferSchema=True)
        _, test_data = df.randomSplit([0.7, 0.3], seed=42)

        # Ścieżki do modeli (upewnij się, że foldery istnieją w tej lokalizacji)
        path_v1 = "wind_turbine_model_v1"
        path_opt = "wind_turbine_best_model_optimized"

        if not os.path.exists(path_v1) or not os.path.exists(path_opt):
            print(f"BŁĄD: Nie znaleziono jednego z modeli. Sprawdź ścieżki: {path_v1}, {path_opt}")
            return

        # 4. WCZYTYWANIE MODELI
        print(f"Wczytywanie modelu bazowego z: {path_v1}")
        model_v1 = PipelineModel.load(path_v1)

        print(f"Wczytywanie modelu zoptymalizowanego z: {path_opt}")
        model_opt = PipelineModel.load(path_opt)

        # 5. EWALUACJA
        evaluator = MulticlassClassificationEvaluator(labelCol="label_id", predictionCol="prediction")

        # Metryki, o które prosiłeś
        metrics_setup = [
            ("Accuracy", "accuracy"),
            ("F1-Score", "f1"),
            ("Recall", "weightedRecall")
        ]

        results = []

        for name, model in [("Bazowy", model_v1), ("Zoptymalizowany", model_opt)]:
            print(f"Przetwarzanie metryk dla: {name}...")
            predictions = model.transform(test_data)

            row = {"Model": name}
            for label, metric_internal_name in metrics_setup:
                score = evaluator.evaluate(predictions, {evaluator.metricName: metric_internal_name})
                row[label] = round(score, 4)
            results.append(row)

        # 6. WYŚWIETLENIE I ZAPIS WYNIKÓW
        comparison_df = pd.DataFrame(results)

        print("\n" + "=" * 50)
        print("PORÓWNANIE SKUTECZNOŚCI MODELI")
        print("=" * 50)
        print(comparison_df.to_string(index=False))
        print("=" * 50)

        # Obliczenie zysku
        acc_diff = (comparison_df.iloc[1]["Accuracy"] - comparison_df.iloc[0]["Accuracy"]) * 100
        f1_diff = (comparison_df.iloc[1]["F1-Score"] - comparison_df.iloc[0]["F1-Score"]) * 100

        print(f"Zysk Accuracy po optymalizacji: {acc_diff:.2f} pkt proc.")
        print(f"Zysk F1-Score po optymalizacji: {f1_diff:.2f} pkt proc.")

        comparison_df.to_csv("tabela_porownawcza_final.csv", index=False)
        print("\nWyniki zapisano do: tabela_porownawcza_final.csv")

    except Exception as e:
        print(f"Błąd krytyczny: {e}")
    finally:
        spark.stop()


if __name__ == "__main__":
    run_comparison()