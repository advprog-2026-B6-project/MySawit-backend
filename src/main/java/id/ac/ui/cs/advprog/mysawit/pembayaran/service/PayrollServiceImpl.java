package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final UserRepository userRepository;
    private final WageSettingService wageSettingService;

    // 90% logic meaning multiplier is 0.9
    private static final BigDecimal WAGE_MULTIPLIER = new BigDecimal("0.90");

    public PayrollServiceImpl(PayrollRepository payrollRepository,
                              UserRepository userRepository,
                              WageSettingService wageSettingService) {
        this.payrollRepository = payrollRepository;
        this.userRepository = userRepository;
        this.wageSettingService = wageSettingService;
    }

    @Override
    public java.math.BigDecimal calculateWage(String roleStr, BigDecimal totalKg) {
        if (totalKg == null || totalKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        WageSetting wageSetting = wageSettingService.getWageSetting();
        BigDecimal ratePerKg;

        switch (roleStr) {
            case "BURUH":
                ratePerKg = wageSetting.getUpahBuruhPerKg();
                break;
            case "SUPIR":
                ratePerKg = wageSetting.getUpahSupirPerKg();
                break;
            case "MANDOR":
                ratePerKg = wageSetting.getUpahMandorPerKg();
                break;
            default:
                ratePerKg = BigDecimal.ZERO;
        }

        if (ratePerKg == null) {
            ratePerKg = BigDecimal.ZERO;
        }

        // Calculation: totalKg * ratePerKg * 90%
        BigDecimal baseWage = totalKg.multiply(ratePerKg);
        return baseWage.multiply(WAGE_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public PayrollResponse createPayroll(PayrollCreateRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                    "User not found with username: " + request.getUsername()));
                
        String roleStr = user.getRole() != null ? user.getRole().name() : "";

        BigDecimal calculatedWage = calculateWage(roleStr, request.getTotalKg());

        Payroll payroll = Payroll.builder()
                .username(user.getUsername())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalKg(request.getTotalKg() != null ? request.getTotalKg() : BigDecimal.ZERO)
                .totalWage(calculatedWage)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    @Override
    public List<PayrollResponse> getPayrollsByUsernameForAdmin(
        String username, LocalDate startDate, LocalDate endDate) {

        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "User not found: " + username);
        }
        List<Payroll> payrolls = payrollRepository
        .findByUsernameAndDateFilter(username, startDate, endDate);
        return payrolls.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<PayrollResponse> getPayrollsForWorker(
        String username, LocalDate startDate, LocalDate endDate, String status) {
        List<Payroll> payrolls = payrollRepository.findByUsernameAndFilter(username, startDate, endDate, status);
        return payrolls.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .username(payroll.getUsername())
                .startDate(payroll.getStartDate())
                .endDate(payroll.getEndDate())
                .totalKg(payroll.getTotalKg())
                .totalWage(payroll.getTotalWage())
                .status(payroll.getStatus())
                .createdAt(payroll.getCreatedAt())
                .build();
    }
}
