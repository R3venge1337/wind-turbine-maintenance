package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.SparkFeaturesDto;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
class SimulationProcessor {
    private final KafkaTemplate<String, SparkFeaturesDto> kafkaTemplate;
    private final MeasurementRepository measurementRepository;
    private final PhysicsEngine physicsEngine;
    private final TargetLabeler targetLabeler;

    @Value("${spring.kafka.topic-name:wind-measurements}")
    private String topicName;

    @Async
    public void processSingleTurbine(Turbine turbine, GlobalWeatherDto env, Measurement lastState) {
        try {
            PhysicsContext ctx = physicsEngine.calculateNext(turbine, env, lastState);
            TargetLabeler.LabelingResult labelingResult = targetLabeler.label(ctx);

            // 3. Zapisz pomiar (I/O - wirtualny wątek tu "odpoczywa")
            Measurement measurement = Measurement.builder()
                    .turbine(turbine)
                    .environment(new EnvironmentData(ctx.getLocalWind(), ctx.getLocalTempAir()))
                    .operational(
                            new OperationalData(ctx.getRpm(), ctx.getTorque(), ctx.getPowerGenerated(), ctx.getPowerGenerated() * 0.95, ctx.getProcessTemp(), ctx.getTurbine().getCurrentToolWear()))
                    .diagnostics(new DiagnosticFlags(ctx.isDeIcingActive()))
                    .targetLabel(labelingResult.getLabelId())
                    .severity(labelingResult.severity())
                    .timestamp(LocalDateTime.now())
                    .build();

            Measurement mEntity = measurementRepository.save(measurement);

            SparkFeaturesDto sparkContext = new SparkFeaturesDto(
                    turbine.getId(), turbine.getProductId(), mEntity.getId(), turbine.getSettings().getCode().name(), ctx.getLocalWind(), ctx.getLocalTempAir(), ctx.getProcessTemp(), ctx.getRpm(), ctx.getPowerGenerated(), ctx.getTorque(), turbine.getCurrentToolWear(), mEntity.getTimestamp()
            );

            // 3. Wysyłka na Kafkę (surowe dane dla PySparka / Frontendu)
            // Używamy productId jako klucza, aby dane z tej samej turbiny trafiały do tej samej partycji
            kafkaTemplate.send(topicName, turbine.getProductId(), sparkContext);

            log.debug("Measurement sent to Kafka and DB for turbine: {}", turbine.getProductId());

        } catch (Exception e) {
            log.error("Failed to process turbine {}: {}", turbine.getProductId(), e.getMessage());
        }
    }


}
