package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.BuruhWageStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.MandorWageStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.SupirWageStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyTest {

    @Test
    void testBuruhStrategy() {
        BuruhWageStrategy strategy = new BuruhWageStrategy();
        WageSetting setting = new WageSetting();
        setting.setUpahBuruhPerKg(new BigDecimal("100"));

        BigDecimal result = strategy.calculate(new BigDecimal("10"), setting);
        assertEquals(new BigDecimal("900.00"), result);
    }

    @Test
    void testSupirStrategy() {
        SupirWageStrategy strategy = new SupirWageStrategy();
        WageSetting setting = new WageSetting();
        setting.setUpahSupirPerKg(new BigDecimal("200"));

        BigDecimal result = strategy.calculate(new BigDecimal("10"), setting);
        assertEquals(new BigDecimal("1800.00"), result);
    }

    @Test
    void testMandorStrategy() {
        MandorWageStrategy strategy = new MandorWageStrategy();
        WageSetting setting = new WageSetting();
        setting.setUpahMandorPerKg(new BigDecimal("300"));

        BigDecimal result = strategy.calculate(new BigDecimal("10"), setting);
        assertEquals(new BigDecimal("2700.00"), result);
    }
}