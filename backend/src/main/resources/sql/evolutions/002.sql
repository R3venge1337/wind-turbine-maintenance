--liquibase formatted sql
--changeset adam.zimny:2 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------
CREATE TABLE environment_history (
         id BIGSERIAL PRIMARY KEY,
         timestamp TIMESTAMP NOT NULL,
         global_wind_speed DOUBLE PRECISION NOT NULL,
         global_air_temp DOUBLE PRECISION NOT NULL,
         target_wind_speed DOUBLE PRECISION NOT NULL,
         target_air_temp DOUBLE PRECISION NOT NULL
);

-- Dodanie kolumny dla mocy znamionowej (Rated Power)
ALTER TABLE turbine_types
    ADD COLUMN IF NOT EXISTS rated_power DOUBLE PRECISION;

-- Dodanie kolumny dla kosztu energii odladzania (De-Icing)
ALTER TABLE turbine_types
    ADD COLUMN IF NOT EXISTS deicing_cost DOUBLE PRECISION;

ALTER TABLE measurements
    ADD COLUMN IF NOT EXISTS target_label INTEGER NULL ;

--------------------------------------------
--------------- CONSTRAINTS ----------------
--------------------------------------------

--------------------------------------------
--------------- INDEXES --------------------
--------------------------------------------

--------------------------------------------
---------------Rollback---------------------
--------------------------------------------

-- rollback DROP TABLE environment_history