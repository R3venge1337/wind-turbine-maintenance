package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.zimnya.wind_turbine_maintenance.common.PageDto;
import pl.zimnya.wind_turbine_maintenance.common.PageableRequest;
import pl.zimnya.wind_turbine_maintenance.common.PageableUtils;
import pl.zimnya.wind_turbine_maintenance.common.exception.AlreadyExistException;
import pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages;
import pl.zimnya.wind_turbine_maintenance.common.exception.NotFoundException;
import pl.zimnya.wind_turbine_maintenance.common.validation.DtoValidator;
import pl.zimnya.wind_turbine_maintenance.turbine.TurbineFacade;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.CreateTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.FilterTurbineForm;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.TurbineView;
import pl.zimnya.wind_turbine_maintenance.turbine.dto.UpdateTurbineForm;

import java.time.LocalDateTime;

import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_NOT_FOUND;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_SETTINGS_NOT_FOUND;
import static pl.zimnya.wind_turbine_maintenance.common.exception.ErrorMessages.TURBINE_WITH_PRODUCT_ID_EXIST;

@Service
@RequiredArgsConstructor
class TurbineService implements TurbineFacade {

    private final TurbineRepository turbineRepository;
    private final TurbineSettingsRepository settingsRepository;


    @Override
    public PageDto<TurbineView> findTurbines(final FilterTurbineForm filterForm, final PageableRequest pageableRequest) {
        DtoValidator.validate(filterForm);
        DtoValidator.validate(pageableRequest);

        final TurbineSpecification specification = new TurbineSpecification(filterForm);
        final Page<TurbineView> turbines = turbineRepository.findAll(specification, PageableUtils.createPageable(pageableRequest))
                .map(this::mapToView);

        return PageableUtils.toDto(turbines);
    }

    @Override
    @Transactional
    public TurbineView createTurbine(final CreateTurbineForm form) {
        DtoValidator.validate(form);
        checkUnique(form.productId());

        TurbineSettings settings = settingsRepository.findByCode(TurbineCode.valueOf(form.typeCode()))
                .orElseThrow(() -> new NotFoundException(TURBINE_SETTINGS_NOT_FOUND));

        Turbine turbine = createTurbine(form.productId(), form.city(), form.latitude(), form.longitude(), settings);

        Turbine saved = turbineRepository.save(turbine);

        return mapToView(saved);
    }

    @Override
    public TurbineView getTurbineByProductId(final String productId) {
        return turbineRepository.getTurbineByProductId(productId).map(this::mapToView)
                .orElseThrow(() -> new NotFoundException(TURBINE_NOT_FOUND, productId));
    }

    @Override
    @Transactional
    public TurbineView updateTurbine(final String productId, final UpdateTurbineForm form) {
        DtoValidator.validate(form);
        Turbine turbine = turbineRepository.getTurbineByProductId(productId)
                .orElseThrow(() -> new NotFoundException(TURBINE_NOT_FOUND));

        checkUnique(form.productId(), turbine.getProductId());

        updateTurbine(form, turbine);

        return mapToView(turbineRepository.save(turbine));
    }

    private void updateTurbine(final UpdateTurbineForm form, Turbine turbine) {
        turbine.setProductId(form.productId());

        setLatitudeIfNotEmpty(form, turbine);

        setLongitudeIfNotEmpty(form, turbine);

        turbine.setCity(form.city());
        turbine.setCurrentToolWear(form.currentToolWear());

        changeTurbineSettings(form, turbine);

        turbine.setLastUpdate(LocalDateTime.now());

        resetingToolWearIfValueIsZero(form, turbine);
    }

    private void setLongitudeIfNotEmpty(final UpdateTurbineForm form, Turbine turbine) {
        if(form.longitude() != null) {
            turbine.setLongitude(form.longitude());
        }
    }

    private  void setLatitudeIfNotEmpty(final UpdateTurbineForm form, Turbine turbine) {
        if(form.latitude() != null){
            turbine.setLatitude(form.latitude());
        }
    }

    private void resetingToolWearIfValueIsZero(final UpdateTurbineForm form, Turbine turbine) {
        if (form.currentToolWear() == 0) {
            turbine.setMainSeverity(SeverityType.GOOD);
            turbine.setCurrentFailureLabel(FailureType.HEALTHY);
            turbine.setDescription("System reset after maintenance");
            turbine.setActive(true);
        }
    }

    private void changeTurbineSettings(final UpdateTurbineForm form,  Turbine turbine) {
        TurbineCode newCode = TurbineCode.valueOf(form.typeCode());
        if (!turbine.getSettings().getCode().equals(newCode)) {
                TurbineSettings newSettings = settingsRepository.findByCode(newCode)
                        .orElseThrow(() -> new NotFoundException(TURBINE_SETTINGS_NOT_FOUND, form.typeCode()));
                turbine.setSettings(newSettings);
        }
    }

    @Override
    @Transactional
    @Modifying
    public void deleteTurbine(final String productId) {
        turbineRepository.deleteByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTurbines() {
        return turbineRepository.count();
    }

    @Override
    @Transactional
    public void resetToolWear(final String productId) {
        Turbine turbine = turbineRepository.getTurbineByProductId(productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.TURBINE_NOT_FOUND, productId));

        turbine.setCurrentToolWear(0);

        turbine.setMainSeverity(SeverityType.GOOD);
        turbine.setCurrentFailureLabel(FailureType.HEALTHY);
        turbine.setDescription(FailureType.resolveDescription(FailureType.HEALTHY.getFailureLabel()));
        turbine.setLastUpdate(LocalDateTime.now());

        turbineRepository.save(turbine);
    }

    private TurbineView mapToView(final Turbine turbine) {
        return new TurbineView(
                turbine.getId(),
                turbine.getProductId(),
                turbine.getSettings().getCode().name(),
                turbine.getSettings().getOsfLimit(),
                turbine.getSettings().getWearIncrement(),
                turbine.getSettings().getRatedPower(),
                turbine.getSettings().getDeicingCost(),
                turbine.getLatitude(),
                turbine.getLongitude(),
                turbine.getCity(),
                turbine.getCurrentToolWear(),
                turbine.getMainSeverity().name(),
                turbine.getCurrentFailureLabel().getFullName(),
                turbine.getCurrentFailureLabel().getFailureMessage(),
                turbine.isActive(),
                turbine.getLastUpdate()
        );
    }

    private void checkUnique(final String formProductId, final String entityProductId) {
        if (!formProductId.equals(entityProductId)) {
            checkUnique(formProductId);
        }
    }

    private void checkUnique(final String productId) {
        if (turbineRepository.existsByProductId(productId)) {
            throw new AlreadyExistException(TURBINE_WITH_PRODUCT_ID_EXIST);
        }
    }

    private Turbine createTurbine(final String productId, final String city, final Double lat, final Double lng, final TurbineSettings settings) {
        LocalDateTime now = LocalDateTime.now();
        return Turbine.builder()
                .productId(productId)
                .city(city)
                .latitude(lat)
                .longitude(lng)
                .settings(settings)
                .currentToolWear(0)
                .mainSeverity(SeverityType.GOOD)
                .currentFailureLabel(FailureType.HEALTHY)
                .description(FailureType.HEALTHY.getCautionMessage())
                .isActive(true)
                .createdAt(now)
                .lastUpdate(now)
                .build();
    }
}
