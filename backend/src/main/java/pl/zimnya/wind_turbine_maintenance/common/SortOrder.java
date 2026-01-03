package pl.zimnya.wind_turbine_maintenance.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortOrder {

    @NotBlank
    private String field;
    private Sort.Direction direction;
}