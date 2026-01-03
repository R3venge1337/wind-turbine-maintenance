package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface TurbineRepository extends JpaRepository<Turbine, Long>, JpaSpecificationExecutor<Turbine> {
    Optional<Turbine> getTurbineByProductId(@Param(value = "productId") final String productId);

    boolean existsByProductId(final String productId);

    void deleteByProductId(final String productId);
}
