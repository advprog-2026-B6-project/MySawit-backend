package id.ac.ui.cs.advprog.mysawit.hasil.service;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilHistoryResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

@Component
public class HasilHistoryResponseMapper {
    private final HasilWorkerDirectory workerDirectory;

    public HasilHistoryResponseMapper(HasilWorkerDirectory workerDirectory) {
        this.workerDirectory = workerDirectory;
    }

    public HasilHistoryResponse toResponse(Hasil report) {
        return new HasilHistoryResponse(
                report.getId(),
                report.getWorkerId(),
                workerDirectory.resolveWorkerName(report.getWorkerId()),
                report.getHasilDate(),
                report.getWeightKg(),
                report.getNews(),
                report.getStatus().name(),
                report.isLocked(),
                report.getPhotoUrls(),
                report.getRejectionReason(),
                report.isVisibleForPengiriman()
        );
    }
}
