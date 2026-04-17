package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceImplTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WageSettingService wageSettingService;

    private PayrollServiceImpl payrollService;

    @BeforeEach
    void setUp() {
        payrollService = new PayrollServiceImpl(payrollRepository, userRepository, wageSettingService);
    }

    @Test
    void testCalculateWage_Buruh() {
        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        // totalKg = 10 -> baseWage = 10 * 100 = 1000
        // expected wage = 1000 * 90% = 900.00
        BigDecimal result = payrollService.calculateWage("BURUH", new BigDecimal("10"));
        assertEquals(new BigDecimal("900.00"), result);
    }

    @Test
    void testCalculateWage_Supir() {
        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        // totalKg = 15 -> baseWage = 15 * 200 = 3000
        // expected wage = 3000 * 0.9 = 2700.00
        BigDecimal result = payrollService.calculateWage("SUPIR", new BigDecimal("15"));
        assertEquals(new BigDecimal("2700.00"), result);
    }

    @Test
    void testCalculateWage_Mandor() {
        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        // totalKg = 5 -> baseWage = 5 * 300 = 1500
        // expected wage = 1500 * 0.9 = 1350.00
        BigDecimal result = payrollService.calculateWage("MANDOR", new BigDecimal("5"));
        assertEquals(new BigDecimal("1350.00"), result);
    }

    @Test
    void testCalculateWage_UnknownRole_ShouldReturnZero() {
        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        BigDecimal result = payrollService.calculateWage("ADMIN", new BigDecimal("10"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void testCalculateWage_ZeroKg_ShouldReturnZero() {
        BigDecimal result = payrollService.calculateWage("BURUH", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateWage_NullKg_ShouldReturnZero() {
        BigDecimal result = payrollService.calculateWage(
            "BURUH", null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateWage_DecimalKgAndScale() {
        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        // totalKg = 10.55 -> base = 10.55 * 100 = 1055.00
        // expected wage = 1055.00 * 0.9 = 949.50
        BigDecimal result = payrollService.calculateWage("BURUH", new BigDecimal("10.55"));
        assertEquals(new BigDecimal("949.50"), result);
    }
}
