package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.zimnya.wind_turbine_maintenance.turbine.EnvironmentFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherForm;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class EnvironmentService implements EnvironmentFacade {

    private final EnvironmentHistoryRepository repository;
    private final Random random = new Random();

    @Setter
    private double targetWindSpeed;
    @Setter
    private double targetAirTemp;

    private double currentGlobalWindSpeed;
    private double currentGlobalAirTemp;

    @PostConstruct
    public void init() {
        repository.findFirstByOrderByTimestampDesc().ifPresentOrElse(
                last -> {
                    log.info("Wczytano ostatni stan pogody z bazy danych.");
                    this.targetWindSpeed = last.getTargetWindSpeed();
                    this.currentGlobalWindSpeed = last.getGlobalWindSpeed();
                    this.targetAirTemp = last.getTargetAirTemp();
                    this.currentGlobalAirTemp = last.getGlobalAirTemp();
                },
                () -> {
                    log.info("Baza historii pogody jest pusta. Inicjalizacja wartościami domyślnymi.");
                    this.targetWindSpeed = 40.0;
                    this.currentGlobalWindSpeed = 40.0;
                    this.targetAirTemp = 15.0;
                    this.currentGlobalAirTemp = 15.0;
                }
        );
    }

    @Override
    public void update(GlobalWeatherForm form) {
        this.targetWindSpeed = form.windSpeed();
        this.targetAirTemp = form.airTemp();
        log.info("Pan farmy zmienił cel na: {} km/h", targetWindSpeed);
    }

    @Transactional
    public GlobalWeatherDto evolveAndSave() {
        // Logika "płynięcia" pogody (bezwładność)
        double windStep = (targetWindSpeed - currentGlobalWindSpeed) * 0.1;
        double tempStep = (targetAirTemp - currentGlobalAirTemp) * 0.05;

        currentGlobalWindSpeed += windStep + (random.nextGaussian() * 0.5);
        currentGlobalAirTemp += tempStep + (random.nextGaussian() * 0.1);
        currentGlobalWindSpeed = Math.max(0.1, currentGlobalWindSpeed);

        EnvironmentHistory history = EnvironmentHistory.builder()
                .timestamp(LocalDateTime.now())
                .globalWindSpeed(currentGlobalWindSpeed)
                .globalAirTemp(currentGlobalAirTemp)
                .targetWindSpeed(targetWindSpeed)
                .targetAirTemp(targetAirTemp)
                .build();

        return mapToWeatherDto(repository.save(history));
    }

    @Override
    public GlobalWeatherDto getCurrentWeather() {
        return new GlobalWeatherDto(currentGlobalWindSpeed, targetWindSpeed, currentGlobalAirTemp, targetAirTemp, LocalDateTime.now());
    }


    GlobalWeatherDto mapToWeatherDto(EnvironmentHistory history) {
        return new GlobalWeatherDto(history.getGlobalWindSpeed(), history.getTargetWindSpeed(), history.getGlobalAirTemp(), history.getTargetAirTemp(), history.getTimestamp());
    }
}
