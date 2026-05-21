package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.ac.ui.cs.advprog.mysawit.hasil.exception.DailySubmissionLimitException;
import id.ac.ui.cs.advprog.mysawit.hasil.factory.HasilFactory;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.payroll.HasilPayrollPublisher;
import id.ac.ui.cs.advprog.mysawit.hasil.payroll.NoOpHasilPayrollPublisher;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilRepository;
import id.ac.ui.cs.advprog.mysawit.hasil.transition.ApproveHasilTransitionStrategy;
import id.ac.ui.cs.advprog.mysawit.hasil.transition.HasilTransitionRegistry;
import id.ac.ui.cs.advprog.mysawit.hasil.transition.HasilTransitionRequest;
import id.ac.ui.cs.advprog.mysawit.hasil.transition.HasilTransitionResult;
import id.ac.ui.cs.advprog.mysawit.hasil.transition.RejectHasilTransitionStrategy;

@Service
public class HasilServiceImpl implements HasilService {
    private final HasilRepository hasilRepository;
    private final HasilFactory hasilFactory;
    private final HasilPayrollPublisher payrollPublisher;
    private final HasilTransitionRegistry transitionRegistry;

    @Autowired
    public HasilServiceImpl(
            HasilRepository hasilRepository,
            HasilFactory hasilFactory,
            HasilPayrollPublisher payrollPublisher,
            HasilTransitionRegistry transitionRegistry
    ) {
        this.hasilRepository = hasilRepository;
        this.hasilFactory = hasilFactory;
        this.payrollPublisher = payrollPublisher;
        this.transitionRegistry = transitionRegistry;
    }

    HasilServiceImpl(HasilRepository hasilRepository, Clock clock) {
        this(
                hasilRepository,
                new HasilFactory(clock),
                new NoOpHasilPayrollPublisher(),
                HasilTransitionRegistry.defaultRegistry()
        );
    }

    HasilServiceImpl(HasilRepository hasilRepository, HasilPayrollPublisher payrollPublisher, Clock clock) {
        this.hasilRepository = hasilRepository;
        this.hasilFactory = new HasilFactory(clock);
        this.payrollPublisher = payrollPublisher;
        this.transitionRegistry = HasilTransitionRegistry.defaultRegistry();
    }

    @Override
    @Transactional
    public Hasil create(String workerId, double kilogram, String news, List<String> photoUrls) {
        Hasil report = hasilFactory.createSubmitted(workerId, kilogram, news, photoUrls);
        if (hasilRepository.existsByWorkerIdAndDate(workerId, report.getHasilDate())) {
            throw new DailySubmissionLimitException("Buruh hanya bisa submit 1 kali per hari");
        }

        try {
            return hasilRepository.save(report);
        } catch (DataIntegrityViolationException exception) {
            throw new DailySubmissionLimitException("Buruh hanya bisa submit 1 kali per hari");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hasil> findAll() {
        return hasilRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hasil> findAvailableForPengiriman() {
        return hasilRepository.findAll().stream()
                .filter(Hasil::isVisibleForPengiriman)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Hasil> findById(String reportId) {
        return hasilRepository.findById(reportId);
    }

    @Override
    @Transactional
    public Hasil approve(String reportId) {
        return transition(reportId, ApproveHasilTransitionStrategy.ACTION, HasilTransitionRequest.empty());
    }

    @Override
    @Transactional
    public Hasil reject(String reportId, String rejectionReason) {
        return transition(reportId, RejectHasilTransitionStrategy.ACTION, new HasilTransitionRequest(rejectionReason));
    }

    @Override
    @Transactional(readOnly = true)
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

    private Hasil transition(String reportId, String action, HasilTransitionRequest request) {
        Hasil report = getSubmittedReport(reportId);
        HasilTransitionResult result = transitionRegistry.get(action).apply(report, request);
        Hasil savedReport = hasilRepository.save(result.report());
        if (result.publishPayroll()) {
            payrollPublisher.publishApproved(savedReport);
        }
        return savedReport;
    }
}



