package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Order(3)
class ProcessTempCalculator implements MetricCalculator{
    private final Random random = new Random();

    @Override
    public void calculate(PhysicsContext ctx) {
        double ratedPower = ctx.getTurbine().getSettings().getRatedPower();
        double currentTorque = ctx.getTorque(); // Pobieramy to, co wyliczył MechanicsCalculator

        // Zabezpieczenie przed dzieleniem przez zero
        double referenceTorque = (currentTorque > 10) ? currentTorque : 40.0;

        // Dynamiczny sufit obrotów dla tej konkretnej maszyny
        double maxRpmForThisModel = (ratedPower * 9.5493) / referenceTorque;

        // 2. OBLICZANIE CIEPŁA
        double currentRpm = ctx.getRpm();

        // Współczynnik obciążenia (ile z siebie daje teraz turbina)
        double loadFactor = currentRpm / maxRpmForThisModel;

        // Nagrzewanie (max 15 stopni przy pełnym obciążeniu)
        double heatGain = loadFactor * 15.0;

        // 3. AKTUALIZACJA TEMPERATURY
        double airTemp = ctx.getLocalTempAir();
        double targetTemp = airTemp + heatGain + (random.nextGaussian() * 0.5);

        // Wygładzanie (bezwładność cieplna)
        double currentProcessTemp = (ctx.getLastProcessTemp() * 0.7) + (targetTemp * 0.3);
        ctx.setProcessTemp(currentProcessTemp);
    }
}
