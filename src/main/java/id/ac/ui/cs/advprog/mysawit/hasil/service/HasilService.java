package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

public interface HasilService {
    Hasil create(String workerId, double kilogram, String news, List<String> photoUrls);

    Optional<Hasil> findByWorkerAndDate(String workerId, LocalDate hasilDate);
}


