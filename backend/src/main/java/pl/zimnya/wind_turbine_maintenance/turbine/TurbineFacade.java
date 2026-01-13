package pl.zimnya.wind_turbine_maintenance.turbine;

import pl.zimnya.wind_turbine_maintenance.common.PageDto;
import pl.zimnya.wind_turbine_maintenance.common.PageableRequest;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.CreateTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.FilterTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.TurbineView;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.UpdateTurbineForm;

public interface TurbineFacade {

    TurbineView createTurbine(final CreateTurbineForm form);

    TurbineView getTurbineByProductId(final String productId);

    TurbineView updateTurbine(final String productId, final UpdateTurbineForm form);

    void deleteTurbine(final String productId);

    PageDto<TurbineView> findTurbines(final FilterTurbineForm filterForm, final PageableRequest pageableRequest);

    long countTurbines();

    void resetToolWear(final String productId);
}
