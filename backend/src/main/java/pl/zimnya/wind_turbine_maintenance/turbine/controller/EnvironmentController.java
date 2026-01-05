package pl.zimnya.wind_turbine_maintenance.turbine.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.zimnya.wind_turbine_maintenance.common.controller.RoutePaths;
import pl.zimnya.wind_turbine_maintenance.turbine.EnvironmentFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherForm;

@RestController
@RequiredArgsConstructor
class EnvironmentController {

    private final EnvironmentFacade environmentFacade;

    @PostMapping(RoutePaths.WEATHER)
    public ResponseEntity<Void> updateWeather(@Valid @RequestBody GlobalWeatherForm form) {
        environmentFacade.update(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping(RoutePaths.WEATHER)
    public ResponseEntity<GlobalWeatherDto> getCurrentWeather() {
        return ResponseEntity.ok(environmentFacade.getCurrentWeather());
    }
}
