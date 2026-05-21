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
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;

@Service
@Primary
public class LocalPayrollRequestClient implements PayrollRequestClient {

    private final PayrollService payrollService;
    private final UserRepository userRepository;
    private final SupirIdentityMapper supirIdentityMapper;

    public LocalPayrollRequestClient(PayrollService payrollService,
                                     UserRepository userRepository,
                                     SupirIdentityMapper supirIdentityMapper) {
        this.payrollService = payrollService;
        this.userRepository = userRepository;
        this.supirIdentityMapper = supirIdentityMapper;
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
                .filter(user -> supirIdentityMapper.toSupirId(user.getUsername()).equals(supirTrukId))
                .map(user -> user.getUsername())
                .findFirst()
                .orElse(null);
    }
}
