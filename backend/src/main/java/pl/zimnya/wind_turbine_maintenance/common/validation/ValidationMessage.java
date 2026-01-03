package pl.zimnya.wind_turbine_maintenance.common.validation;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
public enum ValidationMessage {

    MUST_BE_NULL("{jakarta.validation.constraints.Null.message}"),
    MUST_NOT_BE_NULL("{jakarta.validation.constraints.NotNull.message}"),
    MUST_NOT_BE_EMPTY("{jakarta.validation.constraints.NotEmpty.message}"),
    MUST_NOT_BE_BLANK("{jakarta.validation.constraints.NotBlank.message}"),

    MUST_BE_FALSE("{jakarta.validation.constraints.AssertFalse.message}"),
    MUST_BE_TRUE("{jakarta.validation.constraints.AssertTrue.message}"),

    MUST_BE_SMALLER_THAN_ZERO("{jakarta.validation.constraints.Negative.message}"),
    MUST_BE_SMALLER_THAN_OR_EQUAL_ZERO("{jakarta.validation.constraints.NegativeOrZero.message}"),
    MUST_BE_GREATER_THAN_OR_EQUAL_ZERO("{jakarta.validation.constraints.PositiveOrZero.message}"),
    MUST_BE_GREATER_THAN_ZERO("{jakarta.validation.constraints.Positive.message}"),
    VALUE_TOO_SMALL("{jakarta.validation.constraints.Min.message}"),
    DECIMAL_VALUE_TOO_SMALL("{jakarta.validation.constraints.DecimalMin.message}"),
    VALUE_TOO_BIG("{jakarta.validation.constraints.Max.message}"),
    DECIMAL_VALUE_TOO_BIG("{jakarta.validation.constraints.DecimalMax.message}"),
    MUST_BE_IN_RANGE("{jakarta.validation.constraints.Size.message}"),

    MUST_BE_IN_PAST("{jakarta.validation.constraints.Past.message}"),
    MUST_BE_IN_PAST_OR_PRESENT("{jakarta.validation.constraints.PastOrPresent.message}"),
    MUST_BE_PRESENT_OR_IN_FUTURE("{jakarta.validation.constraints.FutureOrPresent.message}"),
    MUST_BE_IN_FUTURE("{jakarta.validation.constraints.Future.message}"),

    WRONG_NUMBER_OF_DIGITS("{jakarta.validation.constraints.Digits.message}"),
    MUST_MATCH_PATTERN("{jakarta.validation.constraints.Pattern.message}"),
    WRONG_EMAIL_ADDRESS("{jakarta.validation.constraints.Email.message}"),

    INVALID_URL("{org.hibernate.validator.constraints.URL.message}");

    private static final Map<String, String> messages = Arrays.stream(values())
            .collect(toMap(x -> x.code, Enum::name));

    private final String code;

    static String nameByCode(final String code) {
        return messages.getOrDefault(code, "UNKNOWN_VALIDATION_EXCEPTION");
    }
}