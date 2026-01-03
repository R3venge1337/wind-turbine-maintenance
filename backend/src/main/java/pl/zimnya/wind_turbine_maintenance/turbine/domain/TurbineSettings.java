package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.zimnya.wind_turbine_maintenance.common.domain.AbstractEntityId;

@Entity
@Table(name = "turbine_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
class TurbineSettings extends AbstractEntityId {

    @Column(name = "code", unique = true)
    @Enumerated(EnumType.STRING)
    private TurbineCode code;

    @Column(name = "osf_limit", nullable = false)
    private int osfLimit;

    @Column(name = "wear_increment", nullable = false)
    private int wearIncrement;

    @Column(name = "description")
    private String description;
}
