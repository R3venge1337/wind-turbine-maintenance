import os
import sys
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel
from sklearn.metrics import confusion_matrix

# --- KONFIGURACJA ŚCIEŻEK ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable
os.environ['SPARK_HOME'] = os.path.dirname(pyspark.__file__)

jvm_flags = (
    "-Djdk.security.allowGetSubjectCall=true "
    "--add-opens=java.base/java.lang=ALL-UNNAMED "
    "--add-opens=java.base/java.util=ALL-UNNAMED "
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
)


def generate_final_visuals():
    spark = SparkSession.builder \
        .appName("WindSense-Final-Evaluation") \
        .master("local[*]") \
        .config("spark.driver.extraJavaOptions", jvm_flags) \
        .getOrCreate()

    try:
        # 1. Wczytanie modelu i danych
        model_path = "wind_turbine_best_model_optimized"
        csv_path = "turbine_fleet_final.csv"

        print(f"Wczytywanie zoptymalizowanego modelu z: {model_path}")
        model = PipelineModel.load(model_path)
        df = spark.read.csv(csv_path, header=True, inferSchema=True)

        # Podział danych (identyczny seed!)
        _, test_data = df.randomSplit([0.7, 0.3], seed=42)
        predictions = model.transform(test_data)

        # 2. MACIERZ POMYŁEK (Confusion Matrix)
        label_mapping = {
            0: 'HEALTHY', 1: 'HDF_C', 2: 'HDF_F',
            3: 'PWF_C', 4: 'PWF_F', 5: 'OSF_C',
            6: 'OSF_F', 7: 'TWF_C', 8: 'TWF_F', 9: 'RNF_F'
        }

        print("Obliczanie macierzy pomyłek...")
        pdf = predictions.select("label_id", "prediction").toPandas()
        cm = confusion_matrix(pdf["label_id"], pdf["prediction"])

        plt.figure(figsize=(12, 10))
        sns.heatmap(cm, annot=True, fmt="d", cmap="Blues",
                    xticklabels=[label_mapping[i] for i in range(len(label_mapping))],
                    yticklabels=[label_mapping[i] for i in range(len(label_mapping))])
        plt.title("Macierz Pomyłek - Zoptymalizowany RandomForest", fontsize=15)
        plt.xlabel("Przewidziana klasa")
        plt.ylabel("Rzeczywista klasa")
        plt.tight_layout()
        plt.savefig("final_confusion_matrix.png")

        # 3. ISTOTNOŚĆ CECH (Feature Importance)
        print("Analiza ważności cech...")
        # Pobieramy ostatni etap pipeline'u (RandomForest)
        rf_model = model.stages[-1]
        importances = rf_model.featureImportances.toArray()

        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]
        feat_df = pd.DataFrame({'Cecha': feature_cols, 'Istotność': importances})
        feat_df = feat_df.sort_values(by='Istotność', ascending=False)

        plt.figure(figsize=(10, 6))
        sns.barplot(x='Istotność', y='Cecha', data=feat_df, palette='viridis')
        plt.title("Ważność parametrów w detekcji awarii (Best Model)", fontsize=14)
        plt.grid(axis='x', linestyle='--', alpha=0.7)
        plt.tight_layout()
        plt.savefig("final_feature_importance.png")

        print("\nSukces! Wygenerowano pliki:")
        print("- final_confusion_matrix.png")
        print("- final_feature_importance.png")

    except Exception as e:
        print(f"Błąd: {e}")
    finally:
        spark.stop()


if __name__ == "__main__":
    generate_final_visuals()