package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import java.time.LocalDateTime;

public record TurbineView(
        Long id,
        String productId,
        String typeCode,
        int osfLimit,
        int wearIncrement,
        double latitude,
        double longitude,
        String city,
        int currentToolWear,
        String mainSeverity,
        String statusLabel,
        String statusDescription,
        boolean isActive,
        LocalDateTime lastUpdate
) {}
