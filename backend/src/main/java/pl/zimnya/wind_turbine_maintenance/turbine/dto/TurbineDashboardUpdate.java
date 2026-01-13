package pl.zimnya.wind_turbine_maintenance.turbine.dto;

public record TurbineDashboardUpdate(
        TurbineLiveViewDto aiData,
        MeasurementLiveViewDto measurements
) {
}
