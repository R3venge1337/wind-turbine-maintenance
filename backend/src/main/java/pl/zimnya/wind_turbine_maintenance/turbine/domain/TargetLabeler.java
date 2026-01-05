package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class TargetLabeler {

    public LabelingResult label(PhysicsContext ctx) {
        ctx.setTargetLabel(0);
        ctx.setSeverity(SeverityType.GOOD);
        int rpm = ctx.getRpm();
        double torque = ctx.getTorque();
        double toolWear = ctx.getTurbine().getCurrentToolWear();

        double power = ctx.getPowerGenerated();
        double deltaT = ctx.getProcessTemp() - ctx.getLocalTempAir();
        double osfLimit = ctx.getTurbine().getSettings().getOsfLimit();

        log.info("Lokalny wiatr: {}, Lokalna temperatura {},DeltaT: {}, RPM: {}, POWER: {}, OSFLIMIT: {} ",ctx.getLocalWind(),ctx.getLocalTempAir(), deltaT, rpm, power, osfLimit);

        // 1. NAJPIERW SPRAWDZAMY WSZYSTKIE KRYTYCZNE AWARIE (FAILURE)
        if (deltaT < 8.6 && rpm < 1380) {
            log.info("Wynik: {} {}", FailureType.HDF.getFailureLabel(), FailureType.HDF.getFailureMessage());
            return new LabelingResult(FailureType.HDF, SeverityType.FAILURE);

        } else if (power < 3500 || power > 9500) {
            log.info("Wynik: {} {}", FailureType.PWF.getFailureLabel(), FailureType.PWF.getFailureMessage());
            return new LabelingResult(FailureType.PWF, SeverityType.FAILURE);
        } else if (torque * toolWear > osfLimit) {
            log.info("Wynik: {} {}", FailureType.OSF.getFailureLabel(), FailureType.OSF.getFailureMessage());
            return new LabelingResult(FailureType.OSF, SeverityType.FAILURE);
        } else if (toolWear >= 240) {
            log.info("Wynik: {} {}", FailureType.TWF.getFailureLabel(), FailureType.TWF.getFailureMessage());
            return new LabelingResult(FailureType.TWF, SeverityType.FAILURE);
        }

// 2. JEŚLI NIE MA FAILURE, SPRAWDZAMY OSTRZEŻENIA (CAUTION)
        else if (deltaT < 10.0 && rpm < 1500) {
            return new LabelingResult(FailureType.HDF, SeverityType.CAUTION);
        } else if (power < 4000 || power > 9000) {
            return new LabelingResult(FailureType.PWF, SeverityType.CAUTION);
        } else if (torque * toolWear > osfLimit * 0.8) {
            return new LabelingResult(FailureType.OSF, SeverityType.CAUTION);
        } else if (toolWear > 190) {
            return new LabelingResult(FailureType.TWF, SeverityType.CAUTION);
        }

        return new LabelingResult(FailureType.HEALTHY, SeverityType.GOOD);
    }

    public record LabelingResult(FailureType type, SeverityType severity) {
        public int getLabelId() {
            return severity == SeverityType.FAILURE ? type.getFailureLabel() :
                    severity == SeverityType.CAUTION ? type.getCautionLabel() : 0;
        }
    }
}
