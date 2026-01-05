package pl.zimnya.wind_turbine_maintenance.turbine.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class ToolWearService {
    private final TurbineRepository turbineRepository;

    @Transactional
    public void executeIncrement(TurbineCode code) {
        int count = turbineRepository.updateWearForCode(code);
        log.debug("Zaktualizowano {} turbin typu {}", count, code);
    }

}
