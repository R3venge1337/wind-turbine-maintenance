package pl.zimnya.wind_turbine_maintenance.common;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class PageableUtils {

  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final int DEFAULT_PAGE_NUMBER = 0; // Zero indexed spring Pageable

  private static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

  public Pageable createPageable(final PageableRequest pageableRequest) {
    return createPageable(pageableRequest, new HashMap<>(), true);
  }

  public Pageable createPageable(final PageableRequest pageableRequest, final Map<String, String> customMapping, final boolean ignoreCase) {
    if (pageableRequest == null) {
      return DEFAULT_PAGE_REQUEST;
    }

    final int page = pageableRequest.getPage() != null ? oneIndexedToZeroIndexed(pageableRequest.getPage()) : DEFAULT_PAGE_NUMBER;
    final int size = pageableRequest.getSize() != null ? pageableRequest.getSize() : DEFAULT_PAGE_SIZE;

    if (hasSortDirectionWithoutSortingParameter(pageableRequest)) {
      return PageRequest.of(page, size);
    }

    final List<Sort.Order> sortOrders = pageableRequest.getSortBy().stream()
      .map(order -> createOrder(customMapping, order, ignoreCase))
      .collect(toList());

    return PageRequest.of(page, size, Sort.by(sortOrders));
  }

  public <T> PageDto<T> toDto(final Page<T> page) {
    final PageDto<T> dto = new PageDto<>();
    dto.setContent(page.getContent());
    dto.setPageNumber(zeroIndexedToOneIndexed(page.getNumber()));
    dto.setPageSize(page.getSize());
    dto.setTotalElements(page.getTotalElements());
    return dto;
  }

  private static Sort.Order createOrder(final Map<String, String> customMapping, final SortOrder order, final boolean ignoreCase) {
    final Sort.Order sortOrder = new Sort.Order(order.getDirection(), customMapping.getOrDefault(order.getField(), order.getField()));
    return ignoreCase ? sortOrder.ignoreCase() : sortOrder;
  }

  //Spring starts page numeration with 0 and we need to start with page 1
  private int zeroIndexedToOneIndexed(final int pageNumber) {
    return pageNumber + 1;
  }

  private int oneIndexedToZeroIndexed(final int pageNumber) {
    return pageNumber - 1;
  }

  private static boolean hasSortDirectionWithoutSortingParameter(final PageableRequest pageableRequest) {
    return pageableRequest.getSortBy().stream()
      .anyMatch(sortOrder -> isNull(sortOrder.getField()) && nonNull(sortOrder.getDirection()));
  }
}