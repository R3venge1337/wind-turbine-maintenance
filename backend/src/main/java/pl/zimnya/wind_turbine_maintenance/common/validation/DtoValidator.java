package pl.zimnya.wind_turbine_maintenance.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@UtilityClass
public class DtoValidator {
  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public static void validate(final Object dto) {
    final var violations = validator.validate(dto);

    if (isNotEmpty(violations)) {
      throw new ValidationException(Arrays.toString(violations.toArray(ConstraintViolation[]::new)));
    }
  }
}