package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.hasil.exception.DailySubmissionLimitException;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilRepository;

@Service
public class HasilServiceImpl implements HasilService {
    private final HasilRepository hasilRepository;
    private final Clock clock;

    @Autowired
    public HasilServiceImpl(HasilRepository hasilRepository) {
        this(hasilRepository, Clock.systemDefaultZone());
    }

    HasilServiceImpl(HasilRepository hasilRepository, Clock clock) {
        this.hasilRepository = hasilRepository;
        this.clock = clock;
    }

    @Override
    public Hasil create(String workerId, double kilogram, String news, List<String> photoUrls) {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("workerId is required");
        }
        if (kilogram <= 0) {
            throw new IllegalArgumentException("kilogram must be greater than 0");
        }
        if (news == null || news.isBlank()) {
            throw new IllegalArgumentException("news is required");
        }
        if (photoUrls == null || photoUrls.isEmpty()) {
            throw new IllegalArgumentException("at least one photo is required");
        }

        LocalDate today = LocalDate.now(clock);
        if (hasilRepository.existsByWorkerIdAndDate(workerId, today)) {
            throw new DailySubmissionLimitException("Buruh hanya bisa submit 1 kali per hari");
        }

        Hasil report = new Hasil(
                UUID.randomUUID().toString(),
                workerId,
                today,
                kilogram,
                news,
                photoUrls,
                true
        );
        return hasilRepository.save(report);
    }

    @Override
    public Optional<Hasil> findByWorkerAndDate(String workerId, LocalDate hasilDate) {
        return hasilRepository.findByWorkerIdAndDate(workerId, hasilDate);
    }
}



