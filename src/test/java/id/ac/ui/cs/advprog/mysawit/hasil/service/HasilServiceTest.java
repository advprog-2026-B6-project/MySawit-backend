package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilRepository;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.InMemoryHasilRepository;

class HasilServiceTest {
    private HasilService service;
    private FakePayrollService payrollService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-06T01:00:00Z"), ZoneId.of("UTC"));
        payrollService = new FakePayrollService();
        service = new HasilServiceImpl(new InMemoryHasilRepository(), payrollService, fixedClock);
    }

    @Test
    void createFirstSubmissionSuccess() {
        var report = service.create(
                "worker-1",
                120.5,
                "Panen blok A",
                List.of("foto-1.jpg", "foto-2.jpg")
        );

        assertEquals("worker-1", report.getWorkerId());
        assertEquals(120.5, report.getWeightKg());
        assertTrue(report.isLocked());
        assertEquals(HasilStatus.SUBMITTED, report.getStatus());
    }

    @Test
    void createSecondSubmissionSameDayThrowsDailyLimit() {
        service.create(
                "worker-1",
                110.0,
                "Panen pagi",
                List.of("foto-pagi.jpg")
        );

        assertThrows(
                DailySubmissionLimitException.class,
                () -> service.create("worker-1", 90.0, "Panen siang", List.of("foto-siang.jpg"))
        );
    }

    @Test
    void createMapsDatabaseUniqueViolationToDailyLimit() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-06T01:00:00Z"), ZoneId.of("UTC"));
        HasilService duplicateSafeService = new HasilServiceImpl(new DuplicateOnSaveHasilRepository(), fixedClock);

        assertThrows(
                DailySubmissionLimitException.class,
                () -> duplicateSafeService.create("worker-1", 90.0, "Panen siang", List.of("foto-siang.jpg"))
        );
    }

    @Test
    void approveSubmittedReportVerifiesAndPublishesPayrollRequest() throws InterruptedException {
        Hasil report = service.create(
                "worker-1",
                110.0,
                "Panen pagi",
                List.of("foto-pagi.jpg")
        );

        Hasil approved = service.approve(report.getId());

        assertEquals(HasilStatus.VERIFIED, approved.getStatus());
        assertTrue(approved.isVisibleForPengiriman());
        Hasil payrollReport = payrollService.awaitFirstRequest();
        assertEquals("worker-1", payrollReport.getWorkerId());
        assertEquals(110.0, payrollReport.getWeightKg());
    }

    @Test
    void rejectSubmittedReportStoresReasonAndHidesFromPengiriman() {
        Hasil report = service.create(
                "worker-1",
                110.0,
                "Panen pagi",
                List.of("foto-pagi.jpg")
        );

        Hasil rejected = service.reject(report.getId(), "Foto kurang jelas");

        assertEquals(HasilStatus.REJECTED, rejected.getStatus());
        assertEquals("Foto kurang jelas", rejected.getRejectionReason());
        assertEquals(0, service.findAvailableForPengiriman().size());
    }

    @Test
    void approveOrRejectOnlyAllowsSubmittedReports() {
        Hasil report = service.create(
                "worker-1",
                110.0,
                "Panen pagi",
                List.of("foto-pagi.jpg")
        );
        service.reject(report.getId(), "Foto kurang jelas");

        assertThrows(IllegalArgumentException.class, () -> service.approve(report.getId()));
    }

    private static class FakePayrollService implements HasilPayrollPublisher {
        private final List<Hasil> requests = new ArrayList<>();

        @Override
        public void publishApproved(Hasil report) {
            synchronized (requests) {
                requests.add(report);
                requests.notifyAll();
            }
        }

        Hasil awaitFirstRequest() throws InterruptedException {
            synchronized (requests) {
                if (requests.isEmpty()) {
                    requests.wait(TimeUnit.SECONDS.toMillis(2));
                }
                return requests.get(0);
            }
        }
    }

    private static class DuplicateOnSaveHasilRepository implements HasilRepository {
        @Override
        public Hasil save(Hasil report) {
            throw new DataIntegrityViolationException("duplicate worker/date");
        }

        @Override
        public Optional<Hasil> findById(String id) {
            return Optional.empty();
        }

        @Override
        public List<Hasil> findAll() {
            return List.of();
        }

        @Override
        public Optional<Hasil> findByWorkerIdAndDate(String workerId, java.time.LocalDate hasilDate) {
            return Optional.empty();
        }

        @Override
        public boolean existsByWorkerIdAndDate(String workerId, java.time.LocalDate hasilDate) {
            return false;
        }

        @Override
        public PayrollResponse approvePayroll(Long id) {
            return null;
        }

        @Override
        public PayrollResponse rejectPayroll(Long id, String reason) {
            return null;
        }

        @Override
        public java.math.BigDecimal generateMonthlyRecap(int year, int month) {
            return java.math.BigDecimal.ZERO;
        }
    }
}


