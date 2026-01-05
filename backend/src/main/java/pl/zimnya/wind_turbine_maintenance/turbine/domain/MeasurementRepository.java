package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long>, JpaSpecificationExecutor<Measurement> {
    @Query(value = """
        SELECT * FROM (
            SELECT m.*, ROW_NUMBER() OVER (PARTITION BY m.turbine_id ORDER BY m.timestamp DESC) as rn
            FROM measurements m
            WHERE m.turbine_id IN :turbineIds
        ) as sub
        WHERE sub.rn = 1
        """, nativeQuery = true)
    List<Measurement> findLatestForTurbines(@Param("turbineIds") List<Long> turbineIds);
}
