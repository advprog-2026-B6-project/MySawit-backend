package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.mysawit.hasil.exception.DailySubmissionLimitException;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.InMemoryHasilRepository;

class HasilServiceTest {
    private HasilService service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-06T01:00:00Z"), ZoneId.of("UTC"));
        service = new HasilServiceImpl(new InMemoryHasilRepository(), fixedClock);
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
}


