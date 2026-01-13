package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import java.time.LocalDateTime;

public record SparkFeaturesDto(
        Long turbineId,
        String productId,
        Long measurementId,
        String turbineType,
        Double wind_kmh,
        Double local_temp,
        Double process_temp,
        Integer rpm,
        Double power,
        Double torque,
        Integer tool_wear,
        LocalDateTime timestamp
) {
}
