package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

interface TurbineRepository extends JpaRepository<Turbine, Long>, JpaSpecificationExecutor<Turbine> {
    Optional<Turbine> getTurbineByProductId(@Param(value = "productId") final String productId);

    boolean existsByProductId(final String productId);

    void deleteByProductId(final String productId);

    @Query("SELECT t FROM Turbine t JOIN FETCH t.settings WHERE t.isActive = true")
    List<Turbine> findAllActiveWithSettings();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Turbine t SET t.currentToolWear = t.currentToolWear + t.settings.wearIncrement " +
            "WHERE t.isActive = true AND t.settings.code = :code")
    int updateWearForCode(@Param("code") TurbineCode code);
}
