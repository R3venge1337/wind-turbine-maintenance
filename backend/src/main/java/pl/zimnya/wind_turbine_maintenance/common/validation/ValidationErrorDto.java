package pl.zimnya.wind_turbine_maintenance.common.validation;

public record ValidationErrorDto(String field, String message) {
    public ValidationErrorDto(String field, ValidationMessage message) {
        this(field, message.name());
    }

    public ValidationErrorDto(final String message) {
        this(null, message);
    }
}