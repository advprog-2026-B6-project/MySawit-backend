package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryHasilRepository implements HasilRepository {
    private final Map<String, Hasil> reportsById = new ConcurrentHashMap<>();
    private final Map<String, String> reportIdByWorkerAndDate = new ConcurrentHashMap<>();

    @Override
    public Hasil save(Hasil report) {
        reportsById.put(report.getId(), report);
        reportIdByWorkerAndDate.put(buildWorkerDateKey(report.getWorkerId(), report.getHasilDate()), report.getId());
        return report;
    }

    @Override
    public Optional<Hasil> findByWorkerIdAndDate(String workerId, LocalDate hasilDate) {
        String reportId = reportIdByWorkerAndDate.get(buildWorkerDateKey(workerId, hasilDate));
        if (reportId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(reportsById.get(reportId));
    }

    @Override
    public boolean existsByWorkerIdAndDate(String workerId, LocalDate hasilDate) {
        return reportIdByWorkerAndDate.containsKey(buildWorkerDateKey(workerId, hasilDate));
    }

    private String buildWorkerDateKey(String workerId, LocalDate hasilDate) {
        return workerId + ":" + hasilDate;
    }
}


