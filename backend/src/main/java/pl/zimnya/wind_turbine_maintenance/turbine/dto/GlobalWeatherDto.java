package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import java.time.LocalDateTime;

public record GlobalWeatherDto(double globalWindSpeed, double targetWindSpeed, double globalAirTemp, double targetAirTemp, LocalDateTime timestamp) {
}
