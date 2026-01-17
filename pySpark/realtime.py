import os
import sys
from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, col, to_json, struct
from pyspark.sql.types import StructType, StructField, StringType, DoubleType, IntegerType, LongType, ArrayType
from pyspark.ml import PipelineModel

os.environ["HADOOP_HOME"] = r"D:\Biblioteki Programowanie\hadoop"
os.environ["PATH"] += os.pathsep + r"D:\Biblioteki Programowanie\hadoop\bin"
os.environ["JAVA_HOME"] = r"D:\Programy\JDK\openjdk-17.0.1_windows-x64_bin\jdk-17.0.1"
os.environ['PYSPARK_PYTHON'] = sys.executable
os.environ['PYSPARK_DRIVER_PYTHON'] = sys.executable

# KLUCZOWA ZMIANA: Dodajemy flagi do zmiennej środowiskowej sterownika
jvm_flags = (
    "-Djdk.security.allowGetSubjectCall=true "
    "--add-opens=java.base/java.lang=ALL-UNNAMED "
    "--add-opens=java.base/java.util=ALL-UNNAMED "
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
)
os.environ["PYSPARK_SUBMIT_ARGS"] = (
    f'--driver-java-options "{jvm_flags}" '
    '--packages org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.0 '
    'pyspark-shell'
)

# Schemat zgodny z Twoim Java DTO (Spring Boot)
java_dto_schema = StructType([
    StructField("turbineId", LongType()),
    StructField("productId", StringType()),
    StructField("measurementId", LongType()),
    StructField("turbineType", StringType()),
    StructField("wind_kmh", DoubleType()),
    StructField("local_temp", DoubleType()),
    StructField("process_temp", DoubleType()),
    StructField("rpm", IntegerType()),
    StructField("power", DoubleType()),
    StructField("torque", DoubleType()),
    StructField("tool_wear", IntegerType()),
    StructField("timestamp", ArrayType(IntegerType()))
])

def run_windsense_kraft_stream():
    spark = SparkSession.builder \
        .appName("WindSense-KRaft-AI") \
        .master("local[*]") \
        .config("spark.driver.extraJavaOptions", "-Djdk.security.allowGetSubjectCall=true --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/sun.security.action=ALL-UNNAMED") \
        .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    # 1. Wczytanie najlepszego modelu
    model_path = "wind_turbine_best_model_optimized"
    model = PipelineModel.load(model_path)
    print(f"[*] Model załadowany. System gotowy na dane z KRaft.")

    # 2. Odczyt z Kafki (KRaft używa tego samego protokołu co klasyczna Kafka)
    df_kafka = spark.readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("subscribe", "wind-measurements") \
        .option("startingOffsets", "latest") \
        .option("failOnDataLoss", "false") \
        .load()

    # 3. Przetwarzanie JSON
    df_parsed = df_kafka.selectExpr("CAST(value AS STRING)") \
        .select(from_json(col("value"), java_dto_schema).alias("data")) \
        .select("data.*")

    # Przygotowanie do modelu (Rename kolumn)
    df_ml = df_parsed.select(
        col("turbineType").alias("turbine_type"),
        "wind_kmh", "local_temp", "process_temp", "rpm", "power", "torque", "tool_wear",
        "measurementId", "turbineId"
    )

    # 4. Predykcja AI (Inference)
    predictions = model.transform(df_ml)

    console_debug_query = predictions.filter(col("prediction") > 0) \
        .select(
        col("turbineId").alias("ID"),
        col("measurementId").alias("M_ID"),
        col("prediction").cast("integer").alias("AI_PRED"),
        "rpm", "power", "local_temp"  # dodatkowe pola do podglądu
    ) \
        .writeStream \
        .outputMode("append") \
        .format("console") \
        .option("truncate", "false") \
        .start()

    # 5. Wyjście alertów (do Spring Boota przez topik turbine-alerts)
    query = predictions.select(
        to_json(struct(
            col("measurementId"),
            col("turbineId"),
            col("prediction").cast("integer").alias("prediction_ai")
        )).alias("value")
    ).writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("topic", "turbine-alerts") \
        .option("checkpointLocation", "checkpoints/kraft_ai") \
        .start()


    query.awaitTermination()

if __name__ == "__main__":
    run_windsense_kraft_stream()