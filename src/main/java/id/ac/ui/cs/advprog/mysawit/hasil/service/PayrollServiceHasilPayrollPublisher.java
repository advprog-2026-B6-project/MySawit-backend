package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;

@Component
public class PayrollServiceHasilPayrollPublisher implements HasilPayrollPublisher {
    private final PayrollService payrollService;

    public PayrollServiceHasilPayrollPublisher(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @Override
    public void publishApproved(Hasil report) {
        PayrollCreateRequest request = PayrollCreateRequest.builder()
                .username(report.getWorkerId())
                .startDate(report.getHasilDate())
                .endDate(report.getHasilDate())
                .totalKg(BigDecimal.valueOf(report.getWeightKg()))
                .build();
        CompletableFuture.runAsync(() -> payrollService.createPayroll(request));
    }
}
