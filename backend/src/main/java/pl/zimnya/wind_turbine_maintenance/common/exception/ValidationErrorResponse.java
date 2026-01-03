package pl.zimnya.wind_turbine_maintenance.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        Map<String,String> errors,
        LocalDateTime timestamp
) {
}