package id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;

// DESIGN PATTERN: Strategy Pattern
@Component("supir")
public class SupirWageStrategy implements WageCalculationStrategy {
    
    private static final BigDecimal WAGE_MULTIPLIER = new BigDecimal("0.90");

    @Override
    public BigDecimal calculate(BigDecimal totalKg, WageSetting wageSetting) {
        if (totalKg == null || totalKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal rate = wageSetting.getUpahSupirPerKg() != null ? wageSetting.getUpahSupirPerKg() : BigDecimal.ZERO;
        return totalKg.multiply(rate).multiply(WAGE_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }
}
