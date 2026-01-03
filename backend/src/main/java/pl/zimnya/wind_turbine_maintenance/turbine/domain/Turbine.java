package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.zimnya.wind_turbine_maintenance.common.domain.AbstractEntityId;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "turbines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
class Turbine extends AbstractEntityId {

    @Column(name = "product_id", unique = true, nullable = false)
    private String productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "settings_id")
    private TurbineSettings settings;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String city;

    @Column(name = "current_tool_wear")
    private int currentToolWear;

    @Column(name = "main_severity")
    @Enumerated(EnumType.STRING)
    private SeverityType mainSeverity;

    @Column(name = "current_failure_label")
    @Enumerated(EnumType.STRING)
    private FailureType currentFailureLabel;

    @Column(name = "description")
    private String description;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "turbine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements;
}
