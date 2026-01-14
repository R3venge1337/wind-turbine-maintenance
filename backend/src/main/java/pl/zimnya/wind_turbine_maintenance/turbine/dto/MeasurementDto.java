package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import java.time.LocalDateTime;

public record MeasurementDto(
        Long measurementId,
        Long turbineId,
        String productId,
        String turbineType,
        LocalDateTime timestamp,

        // Dane pogodowe i fizyczne
        double windSpeedKmH,
        double airTempCelsius,
        int rpm,
        double torque,
        double powerGenerated,
        double powerNetto,
        double processTempCelsius,
        int toolWear,

        // Flagi diagnostyczne (używamy boolean)
        boolean deIcingActive,
        String severity,
        String targetLabel,
        String predictionAI
) {}
