package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.zimnya.wind_turbine_maintenance.common.PredicateUtils;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.FilterTurbineForm;

import java.util.ArrayList;
import java.util.List;

import static pl.zimnya.wind_turbine_maintenance.common.PredicateUtils.addEqualPredicate;
import static pl.zimnya.wind_turbine_maintenance.common.PredicateUtils.addLikePredicate;

@RequiredArgsConstructor
class TurbineSpecification implements Specification<Turbine> {
    private final FilterTurbineForm filterForm;

    @Override
    public Predicate toPredicate(Root<Turbine> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        addLikePredicate(builder, predicates, root.get(Turbine.Fields.productId), filterForm.productId());

        addLikePredicate(builder, predicates, root.get(Turbine.Fields.city), filterForm.city());

        if (filterForm.typeCode() != null && !filterForm.typeCode().isEmpty()) {
            Join<Turbine, TurbineSettings> settingsJoin = root.join(Turbine.Fields.settings);
            addEqualPredicate(builder, predicates, settingsJoin.get(TurbineSettings.Fields.code), filterForm.typeCode());
        }

        PredicateUtils.addIntegerRangePredicate(
                builder,
                predicates,
                root.get("currentToolWear"),
                filterForm.minToolWear(),
                filterForm.maxToolWear()
        );

        return PredicateUtils.buildAndPredicates(builder, predicates);
    }

}
