package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DtoTest {

    @Test
    void testPayrollCreateRequest() {
        PayrollCreateRequest req = PayrollCreateRequest.builder()
                .username("user")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .totalKg(new BigDecimal("100"))
                .build();

        assertEquals("user", req.getUsername());
        assertNotNull(req.getStartDate());
        assertNotNull(req.getEndDate());
        assertEquals(new BigDecimal("100"), req.getTotalKg());
    }

    @Test
    void testPayrollResponse() {
        PayrollResponse res = PayrollResponse.builder()
                .id(1L)
                .username("user")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .totalKg(new BigDecimal("100"))
                .totalWage(new BigDecimal("500"))
                .status("PENDING")
                .build();

        assertEquals(1L, res.getId());
        assertEquals("user", res.getUsername());
        assertEquals("PENDING", res.getStatus());
        assertEquals(new BigDecimal("100"), res.getTotalKg());
        assertEquals(new BigDecimal("500"), res.getTotalWage());
    }

    @Test
    void testWageUpdateRequest() {
        WageUpdateRequest req = new WageUpdateRequest();
        req.setUpahBuruhPerKg(new BigDecimal("10"));
        req.setUpahSupirPerKg(new BigDecimal("20"));
        req.setUpahMandorPerKg(new BigDecimal("30"));

        assertEquals(new BigDecimal("10"), req.getUpahBuruhPerKg());
        assertEquals(new BigDecimal("20"), req.getUpahSupirPerKg());
        assertEquals(new BigDecimal("30"), req.getUpahMandorPerKg());
    }
}
