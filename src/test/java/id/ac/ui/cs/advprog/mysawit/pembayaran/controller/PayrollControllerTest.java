package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollControllerTest {

    @Mock
    private PayrollService payrollService;

    @InjectMocks
    private PayrollController payrollController;

    private PayrollResponse response;

    @BeforeEach
    void setUp() {
        response = PayrollResponse.builder()
                .id(1L)
                .username("workerA")
                .totalWage(new BigDecimal("1000"))
                .status("PENDING")
                .build();
    }

    @Test
    void testGeneratePayroll() {
        PayrollCreateRequest request = PayrollCreateRequest.builder()
                .username("workerA")
                .build();

        when(payrollService.createPayroll(any(PayrollCreateRequest.class))).thenReturn(response);

        ResponseEntity<PayrollResponse> res = payrollController.generatePayroll(request);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("workerA", res.getBody().getUsername());
    }

    @Test
    void testGetPayrollForUserAsAdmin() {
        when(payrollService.getPayrollsByUsernameForAdmin(eq("workerA"), any(), any()))
                .thenReturn(Collections.singletonList(response));

        ResponseEntity<List<PayrollResponse>> res = payrollController.getPayrollForUserAsAdmin("workerA", null, null);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("workerA", res.getBody().get(0).getUsername());
    }

    @Test
    void testGetMyPayrolls() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("workerA");
        SecurityContextHolder.setContext(securityContext);

        when(payrollService.getPayrollsForWorker(eq("workerA"), any(), any(), eq("PENDING")))
                .thenReturn(Collections.singletonList(response));

        ResponseEntity<List<PayrollResponse>> res = payrollController.getMyPayrolls(null, null, "PENDING");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("workerA", res.getBody().get(0).getUsername());
    }
}
