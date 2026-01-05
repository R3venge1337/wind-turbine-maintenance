package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class SimulationService {

    private final EnvironmentService environmentService;
    private final SimulationProcessor simulationProcessor;
    private final MeasurementRepository measurementRepository;
    private final TurbineRepository turbineRepository;

    // Uruchamia się co 10 sekund
    @Scheduled(fixedRate = 10000)
    public void runSimulationStep() {
        GlobalWeatherDto currentEnv = environmentService.evolveAndSave();

        List<Turbine> turbines = turbineRepository.findAllActiveWithSettings();
        List<Long> ids = turbines.stream().map(Turbine::getId).toList();

        Map<Long, Measurement> latestStates = measurementRepository.findLatestForTurbines(ids)
                .stream()
                .collect(Collectors.toMap(m -> m.getTurbine().getId(), m -> m));

        // 4. Rozsyłamy komplet danych do procesorów
        turbines.forEach(turbine -> {
            Measurement lastState = latestStates.get(turbine.getId());
            simulationProcessor.processSingleTurbine(turbine, currentEnv, lastState);
        });
    }
}
