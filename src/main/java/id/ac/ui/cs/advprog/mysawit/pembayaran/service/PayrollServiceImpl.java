package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.strategy.WageCalculationStrategy;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

@Service
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final UserRepository userRepository;
    private final WageSettingService wageSettingService;
    private final Map<String, WageCalculationStrategy> wageStrategies;

    // 90% logic meaning multiplier is 0.9
    private static final BigDecimal WAGE_MULTIPLIER = new BigDecimal("0.90");

    public PayrollServiceImpl(PayrollRepository payrollRepository,
                              UserRepository userRepository,
                              WageSettingService wageSettingService,
                              Map<String, WageCalculationStrategy> wageStrategies) {
        this.payrollRepository = payrollRepository;
        this.userRepository = userRepository;
        this.wageSettingService = wageSettingService;
        this.wageStrategies = wageStrategies;
    }

    @Override
    public java.math.BigDecimal calculateWage(String roleStr, BigDecimal totalKg) {
        WageSetting wageSetting = wageSettingService.getWageSetting();
        WageCalculationStrategy strategy = wageStrategies.get(roleStr.toLowerCase());
        if (strategy == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return strategy.calculate(totalKg, wageSetting);
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

    @Override
    public PayrollResponse approvePayroll(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found"));
        payroll.approve(); // State Pattern
        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    @Override
    public PayrollResponse rejectPayroll(Long id, String reason) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found"));
        payroll.reject(reason); // State Pattern
        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    @Override
    public BigDecimal generateMonthlyRecap(int year, int month) {
        List<Payroll> allPayrolls = payrollRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        // Intentionally complex calculation for profiling
        for (Payroll p : allPayrolls) {
            if (p.getStartDate().getYear() == year 
                    && p.getStartDate().getMonthValue() == month 
                    && "ACCEPTED".equals(p.getStatus())) {
                BigDecimal wage = p.getTotalWage();
                    // Some artificial complexity for JMH/VisualVM
                    for(int i=0; i<100; i++) {
                        wage = wage.multiply(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);
                    }
                    total = total.add(wage);
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
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
                .rejectReason(payroll.getRejectReason())
                .build();
    }
}
