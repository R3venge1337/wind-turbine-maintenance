package pl.zimnya.wind_turbine_maintenance.turbine.dto;

public record FilterTurbineForm(
        String productId,
        String typeCode,
        String city,
        Integer minToolWear,
        Integer maxToolWear
) {}
