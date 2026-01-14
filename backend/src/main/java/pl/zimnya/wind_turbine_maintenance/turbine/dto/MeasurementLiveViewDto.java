package pl.zimnya.wind_turbine_maintenance.turbine.dto;

public record MeasurementLiveViewDto(
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
        int targetLabel
) {
}
