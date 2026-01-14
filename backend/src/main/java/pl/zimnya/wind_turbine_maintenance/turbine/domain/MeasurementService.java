package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.zimnya.wind_turbine_maintenance.turbine.MeasurementFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.MeasurementDto;

import java.util.List;

@Service
@RequiredArgsConstructor
class MeasurementService implements MeasurementFacade {
    private final MeasurementRepository measurementRepository;

    @Override
    public List<MeasurementDto> findTopTenMeasurements(String productId) {
        return measurementRepository.findLatestRecords(productId, PageRequest.of(0, 10))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public MeasurementDto toDto(Measurement m) {
        return new MeasurementDto(
                m.getId(),
                m.getTurbine().getId(),
                m.getTurbine().getProductId(),
                m.getTurbine().getSettings().getCode().name(),
                m.getTimestamp(),
                m.getEnvironment().getWindSpeedKmh(),
                m.getEnvironment().getAirTempCelsius(),
                m.getOperational().getRpm(),
                m.getOperational().getTorque(),
                m.getOperational().getPowerGenerated(),
                m.getOperational().getPowerGenerated() * 0.95, // Przykład powerNetto
                m.getOperational().getProcessTempCelsius(),
                m.getTurbine().getCurrentToolWear(),
                m.getDiagnostics().isDeIcingActive(),
                m.getSeverity().name(),
                FailureType.fromLabel(m.getTargetLabel()).name(),
                m.getPredictionAi() == null ? null : FailureType.fromLabel(m.getPredictionAi()).name()

        );
    }
}
