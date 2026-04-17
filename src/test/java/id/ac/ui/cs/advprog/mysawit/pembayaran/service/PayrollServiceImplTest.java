package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
    @Test
    void testCreatePayroll_Success() {
        PayrollCreateRequest req = new PayrollCreateRequest();
        req.setUsername("budi");
        req.setTotalKg(new BigDecimal("100"));
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now());

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("budi");
        // Memaksa role null agar fallback ke branch "" teruji
        when(mockUser.getRole()).thenReturn(null); 
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(mockUser));

        WageSetting mockSetting = new WageSetting(
            "1", new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(wageSettingService.getWageSetting()).thenReturn(mockSetting);

        Payroll savedPayroll = new Payroll();
        savedPayroll.setId(1L);
        savedPayroll.setUsername("budi");
        savedPayroll.setTotalWage(BigDecimal.ZERO); // fallback wage
        savedPayroll.setStatus("PENDING");
        
        when(payrollRepository.save(any(Payroll.class))).thenReturn(savedPayroll);

        PayrollResponse response = payrollService.createPayroll(req);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testCreatePayroll_UserNotFound_ThrowsException() {
        PayrollCreateRequest req = new PayrollCreateRequest();
        req.setUsername("hacker");

        when(userRepository.findByUsername("hacker")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> payrollService.createPayroll(req));
    }

    // --- TESTING GET PAYROLLS ---

    @Test
    void testGetPayrollsByUsernameForAdmin_Success() {
        when(userRepository.existsByUsername("budi")).thenReturn(true);
        
        Payroll mockPayroll = new Payroll();
        mockPayroll.setId(99L);
        
        when(payrollRepository.findByUsernameAndDateFilter(
                eq("budi"), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.of(mockPayroll));

        List<PayrollResponse> result = payrollService.getPayrollsByUsernameForAdmin(
                "budi", LocalDate.now(), LocalDate.now());

        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getId());
    }

    @Test
    void testGetPayrollsByUsernameForAdmin_UserNotFound_ThrowsException() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            payrollService.getPayrollsByUsernameForAdmin(
                "ghost", LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testGetPayrollsForWorker_Success() {
        Payroll mockPayroll = new Payroll();
        mockPayroll.setId(55L);

        when(payrollRepository.findByUsernameAndFilter(
                eq("joko"), any(), any(), eq("PAID")))
            .thenReturn(List.of(mockPayroll));

        List<PayrollResponse> result = payrollService.getPayrollsForWorker(
                "joko", LocalDate.now(), LocalDate.now(), "PAID");

        assertEquals(1, result.size());
        assertEquals(55L, result.get(0).getId());
    }
}
