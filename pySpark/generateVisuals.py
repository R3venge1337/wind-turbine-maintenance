import os
import sys
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel
from sklearn.metrics import confusion_matrix

# --- IDENTYCZNA KONFIGURACJA ŚCIEŻEK ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable
os.environ['SPARK_HOME'] = os.path.dirname(pyspark.__file__)

# Flagi JVM naprawiające błąd NativeIO na Windows
jvm_flags = (
    "-Djdk.security.allowGetSubjectCall=true "
    "--add-opens=java.base/java.lang=ALL-UNNAMED "
    "--add-opens=java.base/java.util=ALL-UNNAMED "
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
)


def generate_visualizations():
    print("Inicjalizacja sesji Spark dla wizualizacji...")
    spark = SparkSession.builder \
        .appName("WindSense-Visualizations") \
        .master("local[*]") \
        .config("spark.driver.extraJavaOptions", jvm_flags) \
        .config("spark.executor.extraJavaOptions", jvm_flags) \
        .getOrCreate()

    try:
        model_path = "wind_turbine_model_v1"
        csv_path = "turbine_fleet_final.csv"

        if not os.path.exists(model_path):
            print(f"BŁĄD: Nie znaleziono folderu modelu: {model_path}")
            return

        print("Wczytywanie modelu i danych...")
        model = PipelineModel.load(model_path)
        df = spark.read.csv(csv_path, header=True, inferSchema=True)

        # Podział musi być identyczny jak przy treningu (seed=42)
        _, test_data = df.randomSplit([0.7, 0.3], seed=42)

        print("Generowanie predykcji dla zbioru testowego...")
        predictions = model.transform(test_data)

        # 1. MACIERZ POMYŁEK
        label_mapping = {
            0: 'HEALTHY', 1: 'HDF CAUTION', 2: 'HDF FAILURE',
            3: 'PWF CAUTION', 4: 'PWF FAILURE', 5: 'OSF CAUTION',
            6: 'OSF FAILURE', 7: 'TWF CAUTION', 8: 'TWF FAILURE', 9: 'RNF FAILURE'
        }

        print("Konwersja danych do macierzy pomyłek...")
        pandas_pred = predictions.select("label_id", "prediction").toPandas()
        y_true = pandas_pred["label_id"]
        y_pred = pandas_pred["prediction"]

        cm = confusion_matrix(y_true, y_pred)
        tick_labels = [label_mapping[i] for i in range(len(label_mapping))]

        plt.figure(figsize=(12, 10))
        sns.heatmap(cm, annot=True, fmt="d", cmap="Blues",
                    xticklabels=tick_labels, yticklabels=tick_labels)
        plt.title("Macierz Pomyłek - Statusy Turbin")
        plt.xlabel("Przewidziany Status")
        plt.ylabel("Rzeczywisty Status")
        plt.xticks(rotation=45, ha='right')
        plt.tight_layout()
        plt.savefig("macierz_pomylek.png")
        print("Zapisano: macierz_pomylek.png")

        # 2. ISTOTNOŚĆ CECH (Feature Importance)
        print("Analiza ważności cech...")
        # W Twoim pipeline: 0:Indexer, 1:Assembler, 2:Scaler, 3:RF
        rf_stage = model.stages[3]
        importances = rf_stage.featureImportances.toArray()

        # Uwaga: kolejność musi odpowiadać tej z VectorAssembler
        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]

        feat_imp_df = pd.DataFrame({'Cecha': feature_cols, 'Istotność': importances})
        feat_imp_df = feat_imp_df.sort_values(by='Istotność', ascending=False)

        plt.figure(figsize=(10, 6))
        sns.barplot(x='Istotność', y='Cecha', data=feat_imp_df, palette='magma')
        plt.title("Istotność parametrów w detekcji awarii")
        plt.tight_layout()
        plt.savefig("feature_importance.png")
        print("Zapisano: feature_importance.png")

    except Exception as e:
        print(f"\nWystąpił błąd:\n{e}")
    finally:
        spark.stop()


if __name__ == "__main__":
    generate_visualizations()