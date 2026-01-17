package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum FailureType {
    HEALTHY(0, 0, "Brak awarii", "",""),

    HDF(1, 2, "Heat Dissipation Failure",
            "Temperatura zbliża się do poziomu krytycznego.",
            "Krytyczne przegrzanie - natychmiastowe zatrzymanie."),

    PWF(3, 4, "Power Failure",
            "Niestabilny moment obrotowy.",
            "Awaria zasilania - brak momentu obrotowego."),

    OSF(5, 6, "Over Speed Failure",
            "Obroty powyżej normy roboczej.",
            "Obroty krytyczne - ryzyko uszkodzenia mechanicznego."),

    TWF(7, 8, "Tool Wear Failure",
            "Wysokie zużycie komponentów.",
            "Komponenty zużyte - wymagany serwis."),

    RNF(9, 9, "Random Noise Failure",
            "Nieregularne odczyty czujników.",
            "Błąd krytyczny systemu pomiarowego.");

    private final int cautionLabel;
    private final int failureLabel;
    private final String fullName;
    private final String cautionMessage;
    private final String failureMessage;

    public static String resolveDescription(int label) {
        for (FailureType type : values()) {
            if (label == 0) return HEALTHY.getFailureMessage();
            if (type.cautionLabel == label) return type.getCautionMessage();
            if (type.failureLabel == label) return type.getFailureMessage();
        }
        return "Nieznany kod błędu: " + label;
    }


    public static FailureType fromLabel(int label) {
        if (label == 0) return HEALTHY;
        for (FailureType type : values()) {
            if (type.cautionLabel == label || type.failureLabel == label) {
                return type;
            }
        }
        return HEALTHY;
    }

    public static SeverityType resolveSeverity(int label) {
        if (label == 0) {
            return SeverityType.GOOD;
        }

        // Szukamy, czy to kod typu 'caution' czy 'failure'
        for (FailureType type : values()) {
            if (type.cautionLabel == label) {
                return SeverityType.CAUTION;
            }
            if (type.failureLabel == label) {
                return SeverityType.FAILURE;
            }
        }

        return SeverityType.NONE;
    }
}
