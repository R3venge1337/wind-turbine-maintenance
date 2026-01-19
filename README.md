# WindSense AI - Predictive Maintenance System 🌬️⚙️

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spark](https://img.shields.io/badge/Apache_Spark-3.5-blue)](https://spark.apache.org/)
[![Kafka](https://img.shields.io/badge/Apache_Kafka-3.6-black)](https://kafka.apache.org/)

**WindSense AI** is a professional-grade predictive maintenance platform designed to monitor wind farm health in real-time. By leveraging a high-throughput Big Data pipeline and Machine Learning, the system predicts mechanical failures before they occur, achieving a remarkable **94.99% prediction accuracy**.

---

## 🎯 Project Goals & Research Hypotheses
The project was built to validate two key technical hypotheses:
1. **Low Latency:** Processing thousands of metrics from 50 turbines with sub-second end-to-end latency.
2. **High Precision:** Achieving >90% accuracy in multi-class failure classification using Random Forest algorithms.

## 📊 Data Source & Methodology
The core logic and failure patterns of this system are inspired by the academic standard in predictive maintenance:
* **Base Dataset:** [UCI AI4I 2020 Predictive Maintenance Dataset](https://archive.ics.uci.edu/dataset/601/ai4i+2020+predictive+maintenance+dataset).
* **Adaptation:** The original dataset was transformed into a continuous stream and augmented with a custom **Mathematical Parameter Generation Model**.
* **Wind Context:** We simulated real-time sensor readings for 50 turbines located in the Baltic Sea region (Gdańsk), incorporating aerodynamic efficiency (η) and local environmental conditions.

## 🏗️ System Architecture
The application follows a distributed architecture to ensure scalability and fault tolerance:

1. **Data Source (Spring Boot):** Simulates 50 wind turbines (Types L, M, H) and publishes sensor data to the `wind-measurements` Kafka topic.
2. **Message Broker (Apache Kafka):** Handles high-velocity data streams using the KRaft protocol for optimized performance.
3. **Analytics Engine (PySpark):** A Spark Structured Streaming application that performs real-time feature engineering and inference using a tuned **RandomForestClassifier**.
4. **Command Center (Angular):** A real-time dashboard featuring an interactive map and live alert notifications via WebSockets.

## 🧠 Machine Learning Insights
The system implements a sophisticated "Ground Truth" logic via the `TargetLabeler` class, handling various failure modes:
* **HDF** (Heat Dissipation Failure)
* **PWF** (Power Failure)
* **OSF** (Overstrain Failure)

### Model Optimization
To reach the **94.99% accuracy**, the system utilizes Spark's `CrossValidator` with a `ParamGridBuilder`, optimizing `maxDepth` and `numTrees` for the Random Forest model.

## 🛠️ Tech Stack
| Layer | Technology |
| :--- | :--- |
| **Frontend** | Angular 17+, RxJS, SCSS |
| **Backend** | Java 17, Spring Boot 3, Hibernate |
| **Big Data** | Apache Kafka, Apache Spark (Streaming + MLlib) |
| **Storage** | PostgreSQL, Liquibase (DB Evolutions) |
| **DevOps** | Docker, Docker Compose |

## 📁 Project Structure
* `/backend` - Spring Boot application (Producer & WebSocket Server).
* `/frontend` - Angular dashboard and interactive map.
* `/pySpark` - ML training scripts, Hyperparameter tuning, and Real-time processor.
* `/sql` - Database initialization and evolution scripts.

## ⚡ Execution Sequence

### 1. Configuration (Manual Step)
Since sensitive data is not stored in the repository, you must create two files:
* **`.env`** (in the root directory):
    ```env
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=postgres
    ```
* **`secret.properties`** (in `backend/src/main/resources/`):
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/windTurbineMaintenance?WriteBatchedInserts=true
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    ```

### 2. Infrastructure
Start the message broker and database:
```bash
docker-compose up -d
```

### 3. Machine Learning Pipeline
Generate the dataset and train the models (Required only once):
```bash
cd pySpark
python generateDatasetCSV.py  # Create training data
python main.py                # Train baseline model
python hypertunning.py        # Optimize model to 94.99% accuracy
```

### 4. Real-time Analytics
Start the Spark Streaming processor to listen for Kafka messages:
```bash
python realtime.py
```

### 5. Backend Server
Launch the Spring Boot application (opens WebSocket & API)
```bash
cd ../backend
./mvnw clean install
./mvnw spring-boot:run
```

### 6. Frontend Dashboard
Install dependencies and start the Angular dev server:
```bash
cd ../frontend
npm install
ng serve
```
