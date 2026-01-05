package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zimnya.wind_turbine_maintenance.common.domain.AbstractEntityId;

import java.time.LocalDateTime;

@Entity
@Table(name = "environment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentHistory extends AbstractEntityId {
    private LocalDateTime timestamp;
    private double globalWindSpeed;
    private double globalAirTemp;
    private double targetWindSpeed;
    private double targetAirTemp;
}
