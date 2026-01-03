package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.zimnya.wind_turbine_maintenance.common.domain.AbstractEntityId;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement extends AbstractEntityId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id", nullable = false)
    private Turbine turbine;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Embedded
    private EnvironmentData environment;

    @Embedded
    private OperationalData operational;

    @Embedded
    private DiagnosticFlags diagnostics;

    @Column
    @Enumerated(EnumType.STRING)
    private SeverityType severity;
}
