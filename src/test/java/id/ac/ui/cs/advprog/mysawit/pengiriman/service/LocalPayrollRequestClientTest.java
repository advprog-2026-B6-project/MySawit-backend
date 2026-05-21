package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;

class LocalPayrollRequestClientTest {

    @Test
    void sendPayrollRequest_createsPayrollForSupir() {
        PayrollService payrollService = Mockito.mock(PayrollService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        SupirIdentityMapper supirIdentityMapper = Mockito.mock(SupirIdentityMapper.class);
        LocalPayrollRequestClient client = new LocalPayrollRequestClient(
                payrollService, userRepository, supirIdentityMapper);

        User supir = new User("Supir A", "supir-a", "secret", Role.SUPIR, null);
        UUID supirTrukId = UUID.nameUUIDFromBytes(supir.getUsername().getBytes());
        when(userRepository.findAll()).thenReturn(List.of(supir));
        when(supirIdentityMapper.toSupirId("supir-a")).thenReturn(supirTrukId);

        PayrollRequest request = PayrollRequest.builder()
                .supirTrukId(supirTrukId)
                .muatanKg(120.0)
                .waktuDisetujui(LocalDateTime.of(2026, 5, 2, 9, 0))
                .build();

        client.sendPayrollRequest(request);

        ArgumentCaptor<PayrollCreateRequest> captor = ArgumentCaptor.forClass(PayrollCreateRequest.class);
        verify(payrollService).createPayroll(captor.capture());
        assertEquals("supir-a", captor.getValue().getUsername());
        assertEquals(BigDecimal.valueOf(120.0), captor.getValue().getTotalKg());
    }

    @Test
    void sendPayrollRequest_ignoresWhenSupirNotFound() {
        PayrollService payrollService = Mockito.mock(PayrollService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        SupirIdentityMapper supirIdentityMapper = Mockito.mock(SupirIdentityMapper.class);
        LocalPayrollRequestClient client = new LocalPayrollRequestClient(
                payrollService, userRepository, supirIdentityMapper);

        User mandor = new User("Mandor", "mandor", "secret", Role.MANDOR, null);
        when(userRepository.findAll()).thenReturn(List.of(mandor));
        when(supirIdentityMapper.toSupirId("mandor")).thenReturn(UUID.randomUUID());

        PayrollRequest request = PayrollRequest.builder()
                .supirTrukId(UUID.randomUUID())
                .muatanKg(50.0)
                .waktuDisetujui(LocalDateTime.now())
                .build();

        client.sendPayrollRequest(request);

        verify(payrollService, never()).createPayroll(any());
    }
}
