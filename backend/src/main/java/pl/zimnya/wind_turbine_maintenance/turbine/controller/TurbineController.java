package pl.zimnya.wind_turbine_maintenance.turbine.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.zimnya.wind_turbine_maintenance.common.PageDto;
import pl.zimnya.wind_turbine_maintenance.common.PageableRequest;
import pl.zimnya.wind_turbine_maintenance.common.controller.RoutePaths;
import pl.zimnya.wind_turbine_maintenance.turbine.TurbineFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.CreateTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.FilterTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.TurbineView;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.UpdateTurbineForm;

import java.net.URI;

@RestController
@RequiredArgsConstructor
class TurbineController {
    private final TurbineFacade turbineFacade;

    @PostMapping(RoutePaths.TURBINE + "/search")
    ResponseEntity<PageDto<TurbineView>> findAllTurbines(@Valid @RequestBody final FilterTurbineForm filterForm, final PageableRequest pageableRequest) {
        return ResponseEntity.ok(turbineFacade.findTurbines(filterForm, pageableRequest));
    }

    @PostMapping(RoutePaths.TURBINE)
    ResponseEntity<TurbineView> createTurbine(@RequestBody @Valid final CreateTurbineForm form) {
        final TurbineView turbineCreated = turbineFacade.createTurbine(form);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(turbineCreated.productId())
                .toUri();

        return ResponseEntity.created(location).body(turbineCreated);
    }

    @PutMapping(RoutePaths.TURBINE + "/{productId}")
    ResponseEntity<TurbineView> updateTurbine(
            @PathVariable final String productId,
            @RequestBody @Valid final UpdateTurbineForm form) {

        return ResponseEntity.ok(turbineFacade.updateTurbine(productId, form));
    }

    @GetMapping(RoutePaths.TURBINE + "/count")
    public ResponseEntity<Long> countTurbines() {
        return ResponseEntity.ok(turbineFacade.countTurbines());
    }

    @GetMapping(RoutePaths.TURBINE + "/{productId}")
    ResponseEntity<TurbineView> findTurbineByProductId(@PathVariable final String productId) {
        return ResponseEntity.ok(turbineFacade.getTurbineByProductId(productId));
    }

    @DeleteMapping(RoutePaths.TURBINE + "/{productId}")
    ResponseEntity<Void> delete(@PathVariable final String productId) {
        turbineFacade.deleteTurbine(productId);
        return ResponseEntity.noContent().build();
    }
}
