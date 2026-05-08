package id.ac.ui.cs.advprog.mysawit.hasil.dto;

import java.time.LocalDate;
import java.util.List;

public record HasilHistoryResponse(
        String id,
        String workerId,
        String workerName,
        LocalDate hasilDate,
        double weightKg,
        String news,
        String status,
        boolean locked,
        List<String> photoUrls
) {
}