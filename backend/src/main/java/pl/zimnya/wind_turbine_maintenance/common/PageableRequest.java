package pl.zimnya.wind_turbine_maintenance.common;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
public class PageableRequest {

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    private String sortField;

    private Sort.Direction sortDirection;

    public void addDefaultSort(final SortOrder sortOrder) {
        if (isBlank(sortField)) {
            this.sortField = sortOrder.getField();
            this.sortDirection = sortOrder.getDirection();
        }
    }

    public Optional<SortOrder> getSortBy() {
        if (isBlank(sortField)) {
            return Optional.empty();
        } else {
            return Optional.of(new SortOrder(sortField, sortDirection));
        }
    }
}