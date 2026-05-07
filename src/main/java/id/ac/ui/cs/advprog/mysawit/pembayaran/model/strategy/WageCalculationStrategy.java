package id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy;

import java.math.BigDecimal;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;

// DESIGN PATTERN: Strategy Pattern
public interface WageCalculationStrategy {
    BigDecimal calculate(BigDecimal totalKg, WageSetting wageSetting);
}
