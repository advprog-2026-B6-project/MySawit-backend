package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import java.math.BigDecimal;

public interface WageSettingService {
    WageSetting getWageSetting();
    WageSetting updateWages(BigDecimal upahBuruh, BigDecimal upahSupir, BigDecimal upahMandor);
}