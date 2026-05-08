package id.ac.ui.cs.advprog.mysawit.hasil.model;

import java.time.LocalDate;
import java.util.List;

public record HasilData(
        String id,
        String workerId,
        LocalDate hasilDate,
        double weightKg,
        String news,
        List<String> photoUrls,
        boolean locked,
        HasilStatus status
) {
}