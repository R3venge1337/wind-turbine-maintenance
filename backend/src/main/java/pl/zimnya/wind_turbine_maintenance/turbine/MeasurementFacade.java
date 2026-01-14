package pl.zimnya.wind_turbine_maintenance.turbine;

import pl.zimnya.wind_turbine_maintenance.turbine.dto.MeasurementDto;

import java.util.List;

public interface MeasurementFacade {
    List<MeasurementDto> findTopTenMeasurements(String productId);
}
