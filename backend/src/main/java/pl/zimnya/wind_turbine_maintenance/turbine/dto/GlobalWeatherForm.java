package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_AIR_TEMP_MAX;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_AIR_TEMP_MIN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_AIR_TEMP_NOT_EMPTY;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_WIND_SPEED_MAX;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_WIND_SPEED_MIN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.WEATHER_WIND_SPEED_NOT_EMPTY;

public record GlobalWeatherForm(
        @NotNull(message = WEATHER_WIND_SPEED_NOT_EMPTY)
        @DecimalMin(value = "0.0", message = WEATHER_WIND_SPEED_MIN)
        @DecimalMax(value = "150.0", message = WEATHER_WIND_SPEED_MAX)
        double windSpeed,
        @NotNull(message = WEATHER_AIR_TEMP_NOT_EMPTY)
        @DecimalMin(value = "-30.0", message = WEATHER_AIR_TEMP_MIN)
        @DecimalMax(value = "50.0", message = WEATHER_AIR_TEMP_MAX)
        double airTemp) {
}
