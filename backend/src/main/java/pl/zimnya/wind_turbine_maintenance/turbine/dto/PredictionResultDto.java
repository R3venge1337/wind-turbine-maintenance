package pl.zimnya.wind_turbine_maintenance.turbine.dto;

public record PredictionResultDto(
        Long measurementId,
        Long turbineId,
        String productId,
        Integer prediction_ai
) {}
