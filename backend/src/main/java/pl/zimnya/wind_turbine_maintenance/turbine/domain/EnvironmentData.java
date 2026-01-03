package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class EnvironmentData {
    @Column(name = "wind_speed_kmh")
    private double windSpeedKmh;

    @Column(name = "air_temp_celsius")
    private double airTempCelsius;
}
