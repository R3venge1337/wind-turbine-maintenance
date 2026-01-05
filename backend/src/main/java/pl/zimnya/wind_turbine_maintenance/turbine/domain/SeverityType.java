package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
enum SeverityType {
    GOOD(0),
    CAUTION(1),
    FAILURE(2),
    NONE(3);

    private final int value;

    public static SeverityType fromValue(int value) {
        return Arrays.stream(SeverityType.values())
                .filter(s -> s.value == value)
                .findFirst()
                .orElse(NONE);
    }
}
