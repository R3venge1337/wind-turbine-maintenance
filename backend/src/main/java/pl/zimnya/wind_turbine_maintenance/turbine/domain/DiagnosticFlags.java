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
class DiagnosticFlags {

    @Column(name = "de_icing_active")
    private boolean deIcingActive;
}
