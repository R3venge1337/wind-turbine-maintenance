--liquibase formatted sql
--changeset adam.zimny:1 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------
CREATE TABLE turbine_types (
        id BIGSERIAL PRIMARY KEY,
        code VARCHAR(1) UNIQUE , -- L, M, H
        osf_limit INTEGER NOT NULL,       -- 11000, 12000, 13000
        wear_increment INTEGER NOT NULL,  -- 2, 3, 5
        description VARCHAR(255)
);

CREATE TABLE turbines (
        id BIGSERIAL PRIMARY KEY,
        product_id VARCHAR(50) UNIQUE NOT NULL,
        settings_id BIGINT,
        latitude DOUBLE PRECISION NOT NULL,
        longitude DOUBLE PRECISION NOT NULL,
        city VARCHAR(100),
        current_tool_wear INTEGER DEFAULT 0,
        main_severity VARCHAR(20) NOT NULL,
        current_failure_label VARCHAR(50) NOT NULL,
        description TEXT,
        last_update TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        is_active BOOLEAN DEFAULT TRUE
);

-- Historical measurement table
CREATE TABLE measurements (
        id BIGSERIAL PRIMARY KEY,
        turbine_id BIGINT,
        timestamp TIMESTAMP NOT NULL,

    -- Background
        wind_speed_kmh DOUBLE PRECISION,
        air_temp_celsius DOUBLE PRECISION,

    -- Operational
        rpm INTEGER,
        torque DOUBLE PRECISION,
        power_generated DOUBLE PRECISION,
        power_netto DOUBLE PRECISION,
        process_temp_celsius DOUBLE PRECISION,
        tool_wear INTEGER,

    -- Diagnostic
        de_icing_active BOOLEAN DEFAULT FALSE,
        severity VARCHAR(20) -- GOOD, CAUTION, FAILURE
);

--------------------------------------------
--------------- CONSTRAINTS ----------------
--------------------------------------------

ALTER TABLE turbines
    ADD CONSTRAINT fk_turbines_settings
        FOREIGN KEY (settings_id) REFERENCES turbine_types(id);

ALTER TABLE measurements
    ADD CONSTRAINT fk_measurements_turbine
        FOREIGN KEY (turbine_id) REFERENCES turbines(id) ON DELETE CASCADE;

--------------------------------------------
--------------- INDEXES --------------------
--------------------------------------------

CREATE INDEX idx_measurements_turbine_time ON measurements (turbine_id, timestamp DESC);

--------------------------------------------
---------------Rollback---------------------
--------------------------------------------
-- rollback DROP TABLE measurements;
-- rollback DROP TABLE turbines;