import os
import sys
import pandas as pd
import numpy as np  # Dodano ten import
import matplotlib.pyplot as plt
import seaborn as sns
import pyspark
from pyspark.sql import SparkSession
from pyspark.ml.feature import VectorAssembler, StringIndexer
from pyspark.ml.stat import Correlation

# --- KONFIGURACJA ŚCIEŻEK (Zachowana dla Windows) ---
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


def generate_correlation_heatmap():
    print("Inicjalizacja sesji Spark dla analizy korelacji...")
    spark = SparkSession.builder \
        .appName("WindSense-Correlation-Analysis") \
        .master("local[*]") \
        .config("spark.driver.extraJavaOptions", jvm_flags) \
        .getOrCreate()

    try:
        csv_path = "turbine_fleet_final.csv"
        if not os.path.exists(csv_path):
            print(f"BŁĄD: Nie znaleziono pliku {csv_path}!")
            return

        df = spark.read.csv(csv_path, header=True, inferSchema=True)

        # 1. Przygotowanie cech numerycznych
        indexer = StringIndexer(inputCol="turbine_type", outputCol="type_index")
        df_indexed = indexer.fit(df).transform(df)

        feature_cols = ["type_index", "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear"]

        assembler = VectorAssembler(inputCols=feature_cols, outputCol="features")
        df_vector = assembler.transform(df_indexed)

        # 2. Obliczenie korelacji
        print("Obliczanie macierzy korelacji...")
        matrix = Correlation.corr(df_vector, "features").head()
        corr_matrix = matrix[0].toArray()

        # 3. Konwersja do Pandas
        corr_df = pd.DataFrame(corr_matrix, index=feature_cols, columns=feature_cols)

        # 4. Generowanie Heatmapy (Naprawiony błąd maski)
        plt.figure(figsize=(12, 10))

        # Rysujemy pełną macierz (bez maski)
        sns.heatmap(corr_df,
                    annot=True,  # Włącza wartości liczbowe
                    fmt=".2f",  # Zaokrąglenie do 2 miejsc po przecinku
                    cmap='coolwarm',  # Kolory: niebieski (ujemna), czerwony (dodatnia)
                    linewidths=0.5,  # Odstępy między kafelkami
                    square=True,  # Kafelki będą idealnymi kwadratami
                    cbar_kws={"shrink": .8},  # Skalowanie paska kolorów
                    center=0)  # Ustawia środek skali kolorów na zero

        plt.title("Macierz korelacji parametrów turbin (Pearson)", fontsize=16, pad=20)
        plt.xticks(rotation=45, ha='right')  # Obraca podpisy osi X dla czytelności
        plt.yticks(rotation=0)  # Podpisy osi Y zostają w poziomie
        plt.tight_layout()

        plt.savefig("korelacja_cech.png")
        print("Sukces: Zapisano plik korelacja_cech.png")
        plt.show()

    except Exception as e:
        print(f"\nWystąpił błąd:\n{e}")
    finally:
        spark.stop()


if __name__ == "__main__":
    generate_correlation_heatmap()