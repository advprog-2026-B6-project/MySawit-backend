package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

@Service
@Primary
public class LocalPayrollRequestClient implements PayrollRequestClient {

    private final PayrollService payrollService;
    private final UserRepository userRepository;

    public LocalPayrollRequestClient(PayrollService payrollService, UserRepository userRepository) {
        this.payrollService = payrollService;
        this.userRepository = userRepository;
    }

    @Override
    public void sendPayrollRequest(PayrollRequest request) {
        String supirUsername = resolveSupirUsername(request.getSupirTrukId());
        if (supirUsername == null) {
            return;
        }

        LocalDate tanggal = request.getWaktuDisetujui() != null
                ? request.getWaktuDisetujui().toLocalDate()
                : LocalDate.now();

        PayrollCreateRequest createRequest = PayrollCreateRequest.builder()
                .username(supirUsername)
                .startDate(tanggal)
                .endDate(tanggal)
                .totalKg(BigDecimal.valueOf(request.getMuatanKg()))
                .build();

        payrollService.createPayroll(createRequest);
    }

    private String resolveSupirUsername(UUID supirTrukId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SUPIR)
                .filter(user -> UUID.nameUUIDFromBytes(user.getUsername().getBytes()).equals(supirTrukId))
                .map(user -> user.getUsername())
                .findFirst()
                .orElse(null);
    }
}
