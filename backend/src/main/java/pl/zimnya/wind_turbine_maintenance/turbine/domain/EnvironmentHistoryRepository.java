package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnvironmentHistoryRepository extends JpaRepository<EnvironmentHistory, Long> {
    Optional<EnvironmentHistory> findFirstByOrderByTimestampDesc();
}
