package id.ac.ui.cs.advprog.mysawit.hasil.service;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasilFactoryTest {
    private HasilFactory factory;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-05-22T05:00:00Z"), ZoneId.of("UTC"));
        factory = new HasilFactory(clock);
    }

    @Test
    void createSubmittedBuildsLockedSubmittedReport() {
        Hasil report = factory.createSubmitted("worker-1", 90.5, "Panen", List.of("foto.jpg"));

        assertEquals("worker-1", report.getWorkerId());
        assertEquals(LocalDate.of(2026, 5, 22), report.getHasilDate());
        assertEquals(90.5, report.getWeightKg());
        assertEquals("Panen", report.getNews());
        assertEquals(List.of("foto.jpg"), report.getPhotoUrls());
        assertTrue(report.isLocked());
        assertEquals(HasilStatus.SUBMITTED, report.getStatus());
        assertFalse(report.isVisibleForPengiriman());
    }

    @Test
    void createSubmittedRejectsBlankWorkerId() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createSubmitted(" ", 90.5, "Panen", List.of("foto.jpg")));
    }

    @Test
    void createSubmittedRejectsNonPositiveKilogram() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createSubmitted("worker-1", 0.0, "Panen", List.of("foto.jpg")));
    }

    @Test
    void createSubmittedRejectsBlankNews() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createSubmitted("worker-1", 90.5, " ", List.of("foto.jpg")));
    }

    @Test
    void createSubmittedRejectsMissingPhotos() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createSubmitted("worker-1", 90.5, "Panen", List.of()));
    }
}
