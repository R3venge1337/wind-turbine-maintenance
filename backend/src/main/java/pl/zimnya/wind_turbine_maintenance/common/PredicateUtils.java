package pl.zimnya.wind_turbine_maintenance.common;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.logging.log4j.util.Strings.isNotBlank;


public final class PredicateUtils {

    private static final String LIKE_CHAR = "%";

    private PredicateUtils() {
    }

    public static Predicate buildAndPredicates(final CriteriaBuilder criteriaBuilder, final Collection<Predicate> predicates) {
        return criteriaBuilder.and(predicates.stream().filter(Objects::nonNull).toArray(Predicate[]::new));
    }

    public static <T> void addEqualPredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<T> fieldPath,
                                             final T value) {
        if (nonNull(value)) {
            predicates.add(criteriaBuilder.equal(fieldPath, value));
        }
    }

    public static void addMultipleLikePredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final List<Path<String>> fieldPaths,
                                                final String value) {
        if (isNotBlank(value)) {
            final Predicate[] likePredicates = fieldPaths.stream()
                    .map(path -> createLikeCaseInsensitivePredicate(criteriaBuilder, path, value))
                    .toArray(Predicate[]::new);
            predicates.add(criteriaBuilder.or(likePredicates));
        }
    }

    public static void addLikePredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<String> fieldPath,
                                        final String value) {
        if (isNotBlank(value)) {
            predicates.add(createLikeCaseInsensitivePredicate(criteriaBuilder, fieldPath, value));
        }
    }

    public static Predicate createLikeCaseInsensitivePredicate(final CriteriaBuilder criteriaBuilder, final Path<String> path,
                                                               final String value) {
        return criteriaBuilder.like(criteriaBuilder.lower(path), LIKE_CHAR + value.toLowerCase() + LIKE_CHAR);
    }

    public static <T> void addInPredicate(final Collection<Predicate> predicates, final Path<T> fieldPath, final Collection<T> values) {
        if (isNotEmpty(values)) {
            predicates.add(fieldPath.in(values));
        }
    }

    public static void addLikePredicateAsFieldString(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<String> fieldPath,
                                                     final String value) {
        if (isNotBlank(value) || nonNull(value)) {
            predicates.add(criteriaBuilder.like(fieldPath.as(String.class), LIKE_CHAR + value.toLowerCase() + LIKE_CHAR));
        }
    }

    public static void addIntegerRangePredicate(final CriteriaBuilder cb, final List<Predicate> predicates,
                                                final Path<Integer> path, Integer min, Integer max) {
        if (min == null && max == null) {
            return;
        }

        if (min != null && max != null && min > max) {
            Integer temp = min;
            min = max;
            max = temp;
        }

        if (min != null && max != null) {
            predicates.add(cb.between(path, min, max));
        } else if (min != null) {
            predicates.add(cb.ge(path, min)); // >= min
        } else {
            predicates.add(cb.le(path, max)); // <= max
        }
    }
}