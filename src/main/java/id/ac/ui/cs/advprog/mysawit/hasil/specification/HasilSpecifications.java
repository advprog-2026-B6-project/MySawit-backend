package id.ac.ui.cs.advprog.mysawit.hasil.specification;

import java.time.LocalDate;
import java.util.Set;

import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.worker.HasilWorkerDirectory;

public final class HasilSpecifications {
    private HasilSpecifications() {
    }

    public static HasilSpecification workerIs(String workerId) {
        return report -> workerId.equals(report.getWorkerId());
    }

    public static HasilSpecification workerIn(Set<String> workerIds) {
        return report -> workerIds.contains(report.getWorkerId());
    }

    public static HasilSpecification dateIs(LocalDate date) {
        return report -> date == null || date.equals(report.getHasilDate());
    }

    public static HasilSpecification dateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return report -> (startDate == null || !report.getHasilDate().isBefore(startDate))
                && (endDate == null || !report.getHasilDate().isAfter(endDate));
    }

    public static HasilSpecification statusIs(HasilStatus status) {
        return report -> status == null || status.equals(report.getStatus());
    }

    public static HasilSpecification visibleForPengiriman() {
        return report -> report.isVisibleForPengiriman();
    }

    public static HasilSpecification workerNameContains(
            String workerName,
            HasilWorkerDirectory workerDirectory
    ) {
        return report -> workerName == null || workerName.isBlank()
                || workerDirectory.resolveWorkerName(report.getWorkerId())
                .toLowerCase()
                .contains(workerName.toLowerCase());
    }

    private static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }
    }
}
