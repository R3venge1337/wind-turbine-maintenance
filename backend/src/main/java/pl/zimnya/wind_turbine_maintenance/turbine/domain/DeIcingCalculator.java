package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
class DeIcingCalculator implements MetricCalculator {
    @Override
    public void calculate(PhysicsContext ctx) {
        boolean active = ctx.getLocalTempAir() < 2.0;
        ctx.setDeIcingActive(active);
    }
}
