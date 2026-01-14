package pl.zimnya.wind_turbine_maintenance.turbine.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.zimnya.wind_turbine_maintenance.turbine.MeasurementFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.MeasurementDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
class MeasurementController {
    private final MeasurementFacade measurementFacade;

    @GetMapping("/turbines/{productId}/measurements")
    public ResponseEntity<List<MeasurementDto>> getLatestMeasurements(@PathVariable String productId) {
        return ResponseEntity.ok(measurementFacade.findTopTenMeasurements(productId));
    }
}
