package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;

@Component
public class HasilFactory {
    private final Clock clock;

    @Autowired
    public HasilFactory() {
        this(Clock.systemDefaultZone());
    }

    public HasilFactory(Clock clock) {
        this.clock = clock;
    }

    public Hasil createSubmitted(String workerId, double kilogram, String news, List<String> photoUrls) {
        validate(workerId, kilogram, news, photoUrls);
        return Hasil.of(
                UUID.randomUUID().toString(),
                workerId,
                LocalDate.now(clock),
                kilogram,
                news,
                photoUrls,
                true,
                HasilStatus.SUBMITTED
        );
    }

    private void validate(String workerId, double kilogram, String news, List<String> photoUrls) {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("harus ada workerId!");
        }
        if (kilogram <= 0) {
            throw new IllegalArgumentException("kilogram harus lebih besar dari 0!");
        }
        if (news == null || news.isBlank()) {
            throw new IllegalArgumentException("harus ada news!");
        }
        if (photoUrls == null || photoUrls.isEmpty()) {
            throw new IllegalArgumentException("harus ada setidaknya satu foto!");
        }
    }
}
