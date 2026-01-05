package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Order(3)
class MechanicsCalculator implements MetricCalculator {
    private final Random random = new Random();

    @Override
    public void calculate(PhysicsContext ctx) {
        double windKmH = ctx.getLocalWind();
        double tempC = ctx.getLocalTempAir();

        // 1. Pobieramy moc znamionową z bazy danych dla konkretnego typu
        // L: 7000W, M: 9000W, H: 10000W
        double ratedPower = ctx.getTurbine().getSettings().getRatedPower();

        // 2. OBLICZANIE WYDAJNOŚCI (Na podstawie Twojej tabeli)
        double efficiency = calculateEfficiency(windKmH);

        // 3. OBLICZANIE MOCY GENEROWANEJ Z WIATRU
        double windPower = ratedPower * efficiency;

        // 4. LOGIKA DE-ICINGU (Odladzania)
        double deIcingConsumption = 0;
        if (tempC < 2.0) {
            // Koszt grzałek zależy od typu (L:800W, M:500W, H:300W)
            ctx.setDeIcingActive(true);
            deIcingConsumption = ctx.getTurbine().getSettings().getDeicingCost();
        }
        // 5. MOC FINALNA (Moc z wiatru minus zużycie własne)
        double finalPower = windPower - deIcingConsumption;

        // Jeśli turbina stoi lub wiatr jest ekstremalny (>90km/h), moc to 0
        if (windKmH < 11 || windKmH > 90) {
            finalPower = 0;
        }

        // 6. MECHANIKA (Moment obrotowy i Obroty)
        // Torque wokół 40Nm zgodnie ze specyfikacją AI4I
        double torque = (finalPower > 0) ? 40.0 + (random.nextGaussian() * 10.0) : 0;
        // Dodatkowo warto dodać zabezpieczenie, by torque nie był ujemny
        if (torque < 5.0 && finalPower > 0) torque = 5.0;

        double rpm = (finalPower > 0) ? (finalPower * 9.5493) / torque : 0;

        // Zapis wyników do kontekstu
        ctx.setPowerGenerated(finalPower);
        ctx.setRpm((int) rpm);
        ctx.setTorque(torque);
    }

    private double calculateEfficiency(double windKmH) {
        if (windKmH < 11) return 0.0; // Poniżej 3 m/s

        if (windKmH >= 11 && windKmH < 18) {
            // Wzrost produkcji (3-5 m/s) -> 10% do 35%
            return 0.10 + (windKmH - 11) * (0.25 / 7);
        }
        else if (windKmH >= 18 && windKmH < 43) {
            // Optymalna produkcja (5-12 m/s) -> 35% do 85%
            return 0.35 + (windKmH - 18) * (0.50 / 25);
        }
        else if (windKmH >= 43 && windKmH <= 90) {
            // Wysoka produkcja (12-25 m/s) -> 85% do 100%
            return 0.85 + (windKmH - 43) * (0.15 / 47);
        }

        return 0.0; // Bezpieczeństwo (25+ m/s)
    }
}