package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

public interface HasilService {
    Hasil create(String workerId, double kilogram, String news, List<String> photoUrls);

    List<Hasil> findAll();

    List<Hasil> findAvailableForPengiriman();

    Optional<Hasil> findById(String reportId);

    Hasil approve(String reportId);

    Hasil reject(String reportId, String rejectionReason);

    Optional<Hasil> findByWorkerAndDate(String workerId, LocalDate hasilDate);
}


