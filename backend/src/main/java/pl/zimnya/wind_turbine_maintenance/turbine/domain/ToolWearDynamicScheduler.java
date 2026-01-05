package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class ToolWearDynamicScheduler implements CommandLineRunner {

    private final TaskScheduler taskScheduler;
    private final TurbineSettingsRepository settingsRepository;
    private final ToolWearService toolWearService;

    @Override
    public void run(String @NonNull ... args) {
        List<TurbineSettings> allSettings = settingsRepository.findAll();

        for (TurbineSettings settings : allSettings) {
            long intervalMs = settings.getWearIncrement() * 60 * 1000L;
            TurbineCode code = settings.getCode();

            taskScheduler.scheduleWithFixedDelay(() -> {
                log.info("Uruchamiam inkrementację dla typu: {} (co {} min)", code, settings.getWearIncrement());
                toolWearService.executeIncrement(code);
            }, Duration.ofMillis(intervalMs));

            log.info("Zarejestrowano dynamiczny harmonogram dla {}: co {} ms", code, intervalMs);
        }
    }
}
