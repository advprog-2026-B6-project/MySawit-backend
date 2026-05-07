package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelTest {

    @Test
    void testPayrollModel() {
        Payroll payroll = new Payroll();
        payroll.setId(123L);
        payroll.setUsername("testuser");
        payroll.setStartDate(LocalDate.now());
        payroll.setEndDate(LocalDate.now().plusDays(1));
        payroll.setTotalKg(new BigDecimal("100"));
        payroll.setTotalWage(new BigDecimal("500"));
        payroll.setStatus("PENDING");

        assertEquals(123L, payroll.getId());
        assertEquals("testuser", payroll.getUsername());
        assertNotNull(payroll.getStartDate());
        assertNotNull(payroll.getEndDate());
        assertEquals(new BigDecimal("100"), payroll.getTotalKg());
        assertEquals(new BigDecimal("500"), payroll.getTotalWage());
        assertEquals("PENDING", payroll.getStatus());
    }

    @Test
    void testWageSettingModel() {
        WageSetting ws = new WageSetting();
        ws.setId("1");
        ws.setUpahBuruhPerKg(new BigDecimal("10"));
        ws.setUpahSupirPerKg(new BigDecimal("20"));
        ws.setUpahMandorPerKg(new BigDecimal("30"));

        assertEquals("1", ws.getId());
        assertEquals(new BigDecimal("10"), ws.getUpahBuruhPerKg());
        assertEquals(new BigDecimal("20"), ws.getUpahSupirPerKg());
        assertEquals(new BigDecimal("30"), ws.getUpahMandorPerKg());
    }
}
