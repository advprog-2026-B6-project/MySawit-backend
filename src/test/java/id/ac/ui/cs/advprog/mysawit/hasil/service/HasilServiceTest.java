package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.mysawit.hasil.exception.DailySubmissionLimitException;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.InMemoryHasilRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;

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
        PayrollCreateRequest request = payrollService.awaitFirstRequest();
        assertEquals("worker-1", request.getUsername());
        assertEquals(0, request.getTotalKg().compareTo(java.math.BigDecimal.valueOf(110.0)));
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

    private static class FakePayrollService implements PayrollService {
        private final List<PayrollCreateRequest> requests = new ArrayList<>();

        @Override
        public PayrollResponse createPayroll(PayrollCreateRequest request) {
            synchronized (requests) {
                requests.add(request);
                requests.notifyAll();
            }
            return null;
        }

        PayrollCreateRequest awaitFirstRequest() throws InterruptedException {
            synchronized (requests) {
                if (requests.isEmpty()) {
                    requests.wait(TimeUnit.SECONDS.toMillis(2));
                }
                return requests.get(0);
            }
        }

        @Override
        public List<PayrollResponse> getPayrollsByUsernameForAdmin(
                String username,
                java.time.LocalDate startDate,
                java.time.LocalDate endDate
        ) {
            return List.of();
        }

        @Override
        public List<PayrollResponse> getPayrollsForWorker(
                String username,
                java.time.LocalDate startDate,
                java.time.LocalDate endDate,
                String status
        ) {
            return List.of();
        }

        @Override
        public java.math.BigDecimal calculateWage(String role, java.math.BigDecimal totalKg) {
            return java.math.BigDecimal.ZERO;
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


