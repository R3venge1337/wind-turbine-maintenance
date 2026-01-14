import os
import sys
from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, col, to_json, struct
from pyspark.sql.types import StructType, StructField, StringType, DoubleType, IntegerType, ArrayType, LongType
from pyspark.ml import PipelineModel

# --- 1. KONFIGURACJA ŚRODOWISKA ---
os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable

# Konfiguracja pakietów Kafka dla Sparka
os.environ["PYSPARK_SUBMIT_ARGS"] = (
    '--conf "spark.driver.extraJavaOptions=-Djdk.security.allowGetSubjectCall=true '
    '--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED" '
    '--packages org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.0 '
    'pyspark-shell'
)

# --- 2. SCHEMAT DANYCH # Dostosowany schemat do Twojego SparkFeaturesDto---

java_dto_schema = StructType([
    StructField("turbineId", LongType()),      # Long w Javie -> LongType
    StructField("productId", StringType()),    # String -> StringType
    StructField("measurementId", LongType()),  # Long -> LongType
    StructField("turbineType", StringType()),  # String -> StringType
    StructField("wind_kmh", DoubleType()),     # Double -> DoubleType
    StructField("local_temp", DoubleType()),
    StructField("process_temp", DoubleType()),
    StructField("rpm", IntegerType()),         # Integer w Javie -> IntegerType
    StructField("power", DoubleType()),
    StructField("torque", DoubleType()),
    StructField("tool_wear", IntegerType()),
    StructField("timestamp", ArrayType(IntegerType())),
])

def start_wind_sense_ai():
    # Inicjalizacja sesji Spark
    spark = SparkSession.builder \
        .appName("WindSense-RealTime-AI") \
        .master("local[*]") \
        .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    print("--- System WindSense AI: Uruchomiono sesję Spark ---")

    # --- 3. WCZYTYWANIE WYTRENOWANEGO MODELU ---
    model_path = r"D:\Studia\Politechnika-Lodzka-2024\Semestr 3\Zaawansowane metody przetwarzania Big Data\Projekt\kod\pySpark\wind_turbine_model_v1"
    print(f"Wczytywanie modelu z: {model_path}")
    model = PipelineModel.load(model_path)

    # --- 4. ODCZYT STRUMIENIA Z KAFKI ---
    raw_stream = spark.readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("subscribe", "wind-measurements") \
        .option("startingOffsets", "latest") \
        .load()

    # --- 5. PARSOWANIE JSON I PRZYGOTOWANIE CECH ---
    parsed_df = raw_stream.selectExpr("CAST(value AS STRING)") \
        .select(from_json(col("value"), java_dto_schema).alias("data")) \
        .select("data.*")

    # Przygotowujemy DataFrame dla modelu (PipelineModel sam obsłuży turbine_type -> type_index)
    ml_ready_df = parsed_df.select(
        col("turbineType").alias("turbine_type"),
        col("wind_kmh"),
        col("local_temp"),
        col("process_temp"),
        col("rpm"),
        col("power"),
        col("torque"),
        col("tool_wear"),
        col("measurementId"),
        col("turbineId"),
        col("productId")
    ).dropna()

    # --- 6. PREDYKCJA ---
    predictions = model.transform(ml_ready_df)

    # --- 7. PRZYGOTOWANIE WYNIKÓW (Pełne do konsoli, lekkie do Kafki) ---
    # Rzutujemy prediction na Integer i nazywamy zgodnie z ustaleniami: prediction_ai
    results_df = predictions.select(
        col("measurementId"),
        col("turbineId"),
        col("productId"),
        col("prediction").cast("integer").alias("prediction_ai"),
        col("rpm"),
        col("process_temp"),
        col("local_temp"),
        col("wind_kmh"),
        col("power"),
        col("torque"),
        col("tool_wear")
    )

    # --- 8. WYJŚCIE (SINK) ---

    # A. Konsola (Debugowanie - pełny podgląd)
    console_query = results_df.writeStream \
        .outputMode("append") \
        .format("console") \
        .option("truncate", "false") \
        .start()

    # B. Kafka (Wysyłamy tylko 4 kluczowe pola w formacie JSON)
    kafka_query = results_df.select(
        to_json(struct(
            "measurementId",
            "turbineId",
            "productId",
            "prediction_ai"
        )).alias("value")
    ).writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("topic", "turbine-alerts") \
        .option("checkpointLocation", "checkpoint_final") \
        .start()

    print("--- System monitorowania działa. Nasłuchiwanie na topiku: 'wind-measurements' ---")
    spark.streams.awaitAnyTermination()

if __name__ == "__main__":
    start_wind_sense_ai()