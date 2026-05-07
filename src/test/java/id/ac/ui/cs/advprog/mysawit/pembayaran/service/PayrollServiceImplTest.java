package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.BuruhWageStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.MandorWageStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.SupirWageStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.WageCalculationStrategy;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private Map<String, WageCalculationStrategy> strategies;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>();
        strategies.put("BURUH", new BuruhWageStrategy());
        strategies.put("SUPIR", new SupirWageStrategy());
        strategies.put("MANDOR", new MandorWageStrategy());
        payrollService = new PayrollServiceImpl(payrollRepository, userRepository, wageSettingService, strategies);
    }

    @Test
    void testCalculateWage_Buruh() {
        WageSetting mockSetting = new WageSetting(
                "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        BigDecimal result = payrollService.calculateWage("BURUH", new BigDecimal("10"));
        assertEquals(new BigDecimal("900.00"), result);
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
    void testCreatePayroll_Success() {
        PayrollCreateRequest req = new PayrollCreateRequest();
        req.setUsername("budi");
        req.setTotalKg(new BigDecimal("100"));
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now());

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("budi");
        when(mockUser.getRole()).thenReturn(Role.BURUH);
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(mockUser));

        WageSetting mockSetting = new WageSetting(
                "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        Payroll savedPayroll = new Payroll();
        savedPayroll.setId(1L);
        savedPayroll.setUsername("budi");
        savedPayroll.setTotalWage(new BigDecimal("9000.00"));
        savedPayroll.setStatus("PENDING");

        when(payrollRepository.save(any(Payroll.class))).thenReturn(savedPayroll);

        PayrollResponse response = payrollService.createPayroll(req);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testApprovePayroll_Success() {
        Payroll mockPayroll = new Payroll();
        mockPayroll.setId(1L);
        mockPayroll.setStatus("PENDING");

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(i -> i.getArguments()[0]);

        PayrollResponse response = payrollService.approvePayroll(1L);
        assertEquals("ACCEPTED", response.getStatus());
    }

    @Test
    void testRejectPayroll_Success() {
        Payroll mockPayroll = new Payroll();
        mockPayroll.setId(1L);
        mockPayroll.setStatus("PENDING");

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(i -> i.getArguments()[0]);

        PayrollResponse response = payrollService.rejectPayroll(1L, "Kurang kg");
        assertEquals("REJECTED", response.getStatus());
    }

    @Test
    void testApprovePayroll_NotFound() {
        when(payrollRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> payrollService.approvePayroll(99L));
    }

    @Test
    void testGenerateMonthlyRecap() {
        Payroll p1 = new Payroll();
        p1.setStartDate(LocalDate.of(2023, 10, 5));
        p1.setStatus("ACCEPTED");
        p1.setTotalWage(new BigDecimal("1000"));

        Payroll p2 = new Payroll();
        p2.setStartDate(LocalDate.of(2023, 10, 15));
        p2.setStatus("PENDING"); // Should be ignored
        p2.setTotalWage(new BigDecimal("2000"));

        Payroll p3 = new Payroll();
        p3.setStartDate(LocalDate.of(2023, 9, 15)); // Wrong month, ignored
        p3.setStatus("ACCEPTED");
        p3.setTotalWage(new BigDecimal("3000"));

        when(payrollRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        BigDecimal total = payrollService.generateMonthlyRecap(2023, 10);
        assertEquals(new BigDecimal("1000.00"), total);
    }
}
