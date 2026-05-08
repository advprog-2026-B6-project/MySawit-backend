package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.hasil.exception.DailySubmissionLimitException;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;

@Service
public class HasilServiceImpl implements HasilService {
    private final HasilRepository hasilRepository;
    private final PayrollService payrollService;
    private final Clock clock;

    @Autowired
    public HasilServiceImpl(HasilRepository hasilRepository, PayrollService payrollService) {
        this(hasilRepository, payrollService, Clock.systemDefaultZone());
    }

    HasilServiceImpl(HasilRepository hasilRepository, Clock clock) {
        this(hasilRepository, null, clock);
    }

    HasilServiceImpl(HasilRepository hasilRepository, PayrollService payrollService, Clock clock) {
        this.hasilRepository = hasilRepository;
        this.payrollService = payrollService;
        this.clock = clock;
    }

    @Override
    public Hasil create(String workerId, double kilogram, String news, List<String> photoUrls) {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("workerId is required");
        }
        if (kilogram <= 0) {
            throw new IllegalArgumentException("kilogram must be greater than 0");
        }
        if (news == null || news.isBlank()) {
            throw new IllegalArgumentException("news is required");
        }
        if (photoUrls == null || photoUrls.isEmpty()) {
            throw new IllegalArgumentException("at least one photo is required");
        }

        LocalDate today = LocalDate.now(clock);
        if (hasilRepository.existsByWorkerIdAndDate(workerId, today)) {
            throw new DailySubmissionLimitException("Buruh hanya bisa submit 1 kali per hari");
        }

        Hasil report = Hasil.of(
            UUID.randomUUID().toString(),
            workerId,
            today,
            kilogram,
            news,
            photoUrls,
            true,
            HasilStatus.SUBMITTED
        );
        return hasilRepository.save(report);
    }

    @Override
    public List<Hasil> findAll() {
        return hasilRepository.findAll();
    }

    @Override
    public List<Hasil> findAvailableForPengiriman() {
        return hasilRepository.findAll().stream()
                .filter(Hasil::isVisibleForPengiriman)
                .toList();
    }

    @Override
    public Hasil approve(String reportId) {
        Hasil report = getSubmittedReport(reportId);
        Hasil approvedReport = hasilRepository.save(report.approveForPengiriman());
        publishPayrollRequest(approvedReport);
        return approvedReport;
    }

    @Override
    public Hasil reject(String reportId, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }
        Hasil report = getSubmittedReport(reportId);
        return hasilRepository.save(report.reject(rejectionReason.trim()));
    }

    @Override
    public Optional<Hasil> findByWorkerAndDate(String workerId, LocalDate hasilDate) {
        return hasilRepository.findByWorkerIdAndDate(workerId, hasilDate);
    }

    private Hasil getSubmittedReport(String reportId) {
        Hasil report = hasilRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("hasil report not found"));
        if (!HasilStatus.SUBMITTED.equals(report.getStatus())) {
            throw new IllegalArgumentException("only SUBMITTED reports can be approved or rejected");
        }
        return report;
    }

    private void publishPayrollRequest(Hasil report) {
        if (payrollService == null) {
            return;
        }

        PayrollCreateRequest request = PayrollCreateRequest.builder()
                .username(report.getWorkerId())
                .startDate(report.getHasilDate())
                .endDate(report.getHasilDate())
                .totalKg(BigDecimal.valueOf(report.getWeightKg()))
                .build();
        CompletableFuture.runAsync(() -> payrollService.createPayroll(request));
    }
}



