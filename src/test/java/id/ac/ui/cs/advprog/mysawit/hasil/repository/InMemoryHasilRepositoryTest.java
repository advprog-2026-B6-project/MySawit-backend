package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHasilRepositoryTest {
    private InMemoryHasilRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHasilRepository();
    }

    @Test
    void saveStoresByIdAndWorkerDate() {
        Hasil report = report("h-1", "worker-1", LocalDate.of(2026, 5, 22));

        Hasil saved = repository.save(report);

        assertSame(report, saved);
        assertSame(report, repository.findById("h-1").orElseThrow());
        assertSame(report, repository.findByWorkerIdAndDate("worker-1", LocalDate.of(2026, 5, 22)).orElseThrow());
        assertTrue(repository.existsByWorkerIdAndDate("worker-1", LocalDate.of(2026, 5, 22)));
    }

    @Test
    void findAllReturnsStoredReports() {
        Hasil first = report("h-1", "worker-1", LocalDate.of(2026, 5, 22));
        Hasil second = report("h-2", "worker-2", LocalDate.of(2026, 5, 23));
        repository.save(first);
        repository.save(second);

        List<Hasil> reports = repository.findAll();

        assertEquals(2, reports.size());
        assertTrue(reports.contains(first));
        assertTrue(reports.contains(second));
    }

    @Test
    void missingReportReturnsEmpty() {
        assertFalse(repository.findById("missing").isPresent());
        assertFalse(repository.findByWorkerIdAndDate("worker-1", LocalDate.of(2026, 5, 22)).isPresent());
        assertFalse(repository.existsByWorkerIdAndDate("worker-1", LocalDate.of(2026, 5, 22)));
    }

    private Hasil report(String id, String workerId, LocalDate date) {
        return Hasil.of(
                id,
                workerId,
                date,
                100.0,
                "Panen",
                List.of("foto.jpg"),
                true,
                HasilStatus.SUBMITTED
        );
    }
}
