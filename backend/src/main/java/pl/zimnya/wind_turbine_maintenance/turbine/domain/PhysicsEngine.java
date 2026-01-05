package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.GlobalWeatherDto;

import java.util.List;

@Component
class PhysicsEngine {
    private final List<MetricCalculator> calculators;

    public PhysicsEngine(List<MetricCalculator> calculators) {
        this.calculators = calculators.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();
    }

    public PhysicsContext calculateNext(Turbine turbine, GlobalWeatherDto env, Measurement lastState) {
        PhysicsContext context = new PhysicsContext(turbine, env, lastState);

        for (MetricCalculator calculator : calculators) {
            calculator.calculate(context);
        }

        return context;
    }
}