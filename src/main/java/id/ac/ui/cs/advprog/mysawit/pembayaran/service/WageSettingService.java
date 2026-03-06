package id.ac.ui.cs.advprog.mysawit.pembayaran.service;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.WageSettingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WageSettingService {

    private final WageSettingRepository repository;

    public WageSettingService(WageSettingRepository repository) {
        this.repository = repository;
    }

    public WageSetting getWageSetting() {
        return repository.findById("DEFAULT")
                .orElseGet(() -> repository.save(
                        new WageSetting("DEFAULT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
                ));
    }

    public WageSetting updateWages(BigDecimal upahBuruh, BigDecimal upahSupir, BigDecimal upahMandor) {
        WageSetting setting = getWageSetting();

        if (upahBuruh != null) setting.setUpahBuruhPerKg(upahBuruh);
        if (upahSupir != null) setting.setUpahSupirPerKg(upahSupir);
        if (upahMandor != null) setting.setUpahMandorPerKg(upahMandor);

        return repository.save(setting);
    }
}