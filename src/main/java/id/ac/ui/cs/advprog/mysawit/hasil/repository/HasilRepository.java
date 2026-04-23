package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface HasilRepository {
    Hasil save(Hasil report);
    
    List<Hasil> findAll();

    Optional<Hasil> findByWorkerIdAndDate(String workerId, LocalDate hasilDate);

    boolean existsByWorkerIdAndDate(String workerId, LocalDate hasilDate);
}


