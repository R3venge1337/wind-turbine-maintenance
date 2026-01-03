package pl.zimnya.wind_turbine_maintenance.common.validation;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class ValidationException extends RuntimeException {

    @Getter
    private final Set<ValidationErrorDto> errorDtos;

    public ValidationException(final ValidationErrorDto... errorDtos) {
        this.errorDtos = Stream.of(errorDtos).collect(toSet());
    }

    ValidationException(final ConstraintViolation<?>... violations) {
        errorDtos = Arrays.stream(violations)
                .map(this::mapToDto)
                .collect(toSet());
    }

    private ValidationErrorDto mapToDto(final ConstraintViolation<?> violation) {
        final String property = violation.getPropertyPath().toString();
        final String reason = mapReason(violation.getMessageTemplate());
        return new ValidationErrorDto(property, reason);
    }

    private String mapReason(final String messageTemplate) {
        if (isJavaxValidationMessage(messageTemplate)) {
            return ValidationMessage.nameByCode(messageTemplate);
        } else {
            return messageTemplate;
        }
    }

    private static boolean isJavaxValidationMessage(final String messageTemplate) {
        return messageTemplate.startsWith("{") && messageTemplate.endsWith("}");
    }

}