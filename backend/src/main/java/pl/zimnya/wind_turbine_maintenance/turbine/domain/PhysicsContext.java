package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.MeasurementDto;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
class PhysicsContext {

    private final Turbine turbine;
    private final GlobalWeatherDto globalWeather;
    private final Measurement lastState;

    private double localWind;
    private double localTempAir;
    private boolean deIcingActive;
    private int rpm;
    private double torque;
    private double powerGenerated;
    private double processTemp;
    private SeverityType severity = SeverityType.GOOD;
    private int targetLabel;

    public double getLastProcessTemp() {
        return (lastState != null) ? lastState.getOperational().getProcessTempCelsius() : globalWeather.globalAirTemp();
    }

    public MeasurementDto toDto() {
        return new MeasurementDto(
                turbine.getId(),
                turbine.getProductId(),
                turbine.getSettings().getCode().name(),
                LocalDateTime.now(),
                localWind,
                localTempAir,
                rpm,
                torque,
                powerGenerated,
                powerGenerated * 0.95, // Przykład powerNetto
                processTemp,
                turbine.getCurrentToolWear(),
                deIcingActive,
                severity.name(),
                targetLabel
        );
    }
}
