package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages;
import pl.zimnya.wind_turbine_maintenance.common.exception.NotFoundException;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.MeasurementLiveViewDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.PredictionResultDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.TurbineDashboardUpdate;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.TurbineLiveViewDto;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
class AiModelConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final MeasurementRepository measurementRepository;
    private final TurbineRepository turbineRepository;

    // Grupa konsumenta powinna być unikalna, by nie kolidować z innymi
    @KafkaListener(topics = "turbine-alerts", groupId = "alert-frontend-group")
    @Transactional
    public void consumeAiPrediction(PredictionResultDto aiResult) {
        measurementRepository.findById(aiResult.measurementId()).ifPresent(m -> {

            int label = aiResult.prediction_ai();

            // 1. Aktualizacja Pomiaru
            m.setPredictionAi(label);
            // Tutaj możemy też ustawić severity pomiaru na podstawie AI
            m.setSeverity(FailureType.resolveSeverity(label));
            Measurement mEntity = measurementRepository.save(m);

            // 2. Aktualizacja Turbiny (na podstawie Smart Enum)
            Turbine turbine = m.getTurbine();

            turbine.setMainSeverity(FailureType.resolveSeverity(label));
            turbine.setCurrentFailureLabel(FailureType.fromLabel(label));
            turbine.setDescription(FailureType.resolveDescription(label));
            turbine.setLastUpdate(LocalDateTime.now());

            Turbine turbineEntity = turbineRepository.save(turbine);

            boolean isAnomaly = !m.getTargetLabel().equals(label);

            TurbineLiveViewDto dashboardData = createTurbineLiveViewDto(m, turbineEntity, turbine, label, isAnomaly);

            MeasurementLiveViewDto measurements = getMeasurementById(mEntity);

            TurbineDashboardUpdate fullUpdate = new TurbineDashboardUpdate(dashboardData, measurements);

            // 4. Wysyłka do Frontendu
            messagingTemplate.convertAndSend("/topic/turbine-updates/" + turbine.getProductId(), fullUpdate);

            log.info("Turbina {} zaktualizowana przez AI. Status: {}, Anomalia: {}",
                    turbine.getProductId(), turbine.getMainSeverity(), isAnomaly);
        });
    }

    private MeasurementLiveViewDto getMeasurementById(Measurement mEntity) {
        return measurementRepository.findById(mEntity.getId())
                .map(this::mapToMeasurementLiveViewDto)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.MEASUREMENT_NOT_FOUND));
    }

    private TurbineLiveViewDto createTurbineLiveViewDto(Measurement m, Turbine turbineEntity, Turbine turbine, int label, boolean isAnomaly) {
        return TurbineLiveViewDto.builder()
                .turbineId(turbineEntity.getId())
                .measurementId(m.getId())
                .productId(turbine.getProductId())
                .turbineType(turbineEntity.getSettings().getCode().name())
                .city(turbine.getCity())
                .mainSeverity(turbineEntity.getMainSeverity().name())
                .targetLabel(FailureType.fromLabel(m.getTargetLabel()).name())
                .predictionAi(FailureType.fromLabel(label).name())
                .aiDescription(turbineEntity.getDescription())
                .lastUpdate(turbine.getLastUpdate())
                .latitude(turbineEntity.getLatitude())
                .longitude(turbineEntity.getLongitude())
                .isActive(turbineEntity.isActive())
                .isAnomaly(isAnomaly)
                .build();
    }

    private MeasurementLiveViewDto mapToMeasurementLiveViewDto(Measurement measurement) {
        return new MeasurementLiveViewDto(measurement.getEnvironment().getWindSpeedKmh(), measurement.getEnvironment().getAirTempCelsius(), measurement.getOperational().getRpm(), measurement.getOperational().getTorque(), measurement.getOperational().getPowerGenerated(), measurement.getOperational().getPowerGenerated() * 0.90, measurement.getOperational().getProcessTempCelsius(), measurement.getOperational().getToolWear(), measurement.getDiagnostics().isDeIcingActive(), measurement.getSeverity().name(), measurement.getTargetLabel());
    }
}
