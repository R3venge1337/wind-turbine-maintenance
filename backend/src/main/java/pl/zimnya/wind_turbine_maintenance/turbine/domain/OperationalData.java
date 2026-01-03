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
class OperationalData {

    @Column(name = "rpm")
    private int rpm;

    @Column(name = "torque")
    private double torque;

    @Column(name = "power_generated")
    private double powerGenerated;

    @Column(name = "power_netto")
    private double powerNetto;

    @Column(name = "process_temp_celsius")
    private double processTempCelsius;

    @Column(name = "tool_wear")
    private int toolWear;
}