package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TurbineLiveViewDto(
        Long turbineId,
        Long measurementId,
        String productId,
        String turbineType,
        String city,
        String mainSeverity,
        String targetLabel,
        String predictionAi,
        String aiDescription,
        LocalDateTime lastUpdate,
        Double latitude,
        Double longitude,
        boolean isActive,
        boolean isAnomaly
) {
}
