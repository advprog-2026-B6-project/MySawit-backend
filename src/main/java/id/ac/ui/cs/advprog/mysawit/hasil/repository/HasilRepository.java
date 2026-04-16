package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

import java.time.LocalDate;
import java.util.Optional;

public interface HasilRepository {
    Hasil save(Hasil report);

    Optional<Hasil> findByWorkerIdAndDate(String workerId, LocalDate hasilDate);

    boolean existsByWorkerIdAndDate(String workerId, LocalDate hasilDate);
}


