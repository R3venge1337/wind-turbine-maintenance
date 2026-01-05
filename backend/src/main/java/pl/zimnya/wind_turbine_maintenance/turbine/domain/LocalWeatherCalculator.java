package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Order(1)
class LocalWeatherCalculator implements MetricCalculator{
    private final Random random = new Random();

    @Override
    public void calculate(PhysicsContext ctx) {
        double localWind = Math.max(0.1, ctx.getGlobalWeather().globalWindSpeed() + (random.nextGaussian() * 1.5));
        double localTemp = ctx.getGlobalWeather().globalAirTemp() + (random.nextGaussian() * 0.5);

        ctx.setLocalWind(localWind);
        ctx.setLocalTempAir(localTemp);
    }
}
