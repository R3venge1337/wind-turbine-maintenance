package pl.zimnya.wind_turbine_maintenance.turbine;

import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherForm;

public interface EnvironmentFacade {
    void update(GlobalWeatherForm form);
    GlobalWeatherDto getCurrentWeather();

}
