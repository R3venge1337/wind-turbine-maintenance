package pl.zimnya.wind_turbine_maintenance.turbine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_CITY_SIZE;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_CURRENT_TOOL_WEAR_MIN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_LATITUDE_MAX;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_LATITUDE_MIN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_LONGITUDE_MAX;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_LONGITUDE_MIN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_PRODUCT_ID_NOT_EMPTY;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_PRODUCT_ID_SIZE;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_TYPE_CODE_PATTERN;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_TYPE_CODE_REQUIRED;

public record UpdateTurbineForm(

        @NotBlank(message = TURBINE_PRODUCT_ID_NOT_EMPTY)
        @Size(max = 50, message = TURBINE_PRODUCT_ID_SIZE)
        String productId,

        @NotBlank(message = TURBINE_TYPE_CODE_REQUIRED)
        @Pattern(regexp = "^[LMH]$", message = TURBINE_TYPE_CODE_PATTERN)
        String typeCode,

        @Min(value = -90, message = TURBINE_LATITUDE_MIN)
        @Max(value = 90, message = TURBINE_LATITUDE_MAX)
        Double latitude,

        @Min(value = -180, message = TURBINE_LONGITUDE_MIN)
        @Max(value = 180, message = TURBINE_LONGITUDE_MAX)
        Double longitude,

        @Size(max = 100, message = TURBINE_CITY_SIZE)
        String city,

        @Min(value = 0, message = TURBINE_CURRENT_TOOL_WEAR_MIN)
        Integer currentToolWear
) {
}
