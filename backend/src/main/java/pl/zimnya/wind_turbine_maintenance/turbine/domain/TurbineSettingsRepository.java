package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TurbineSettingsRepository extends JpaRepository<TurbineSettings, Long> {
    Optional<TurbineSettings> findByCode(TurbineCode code);
}
