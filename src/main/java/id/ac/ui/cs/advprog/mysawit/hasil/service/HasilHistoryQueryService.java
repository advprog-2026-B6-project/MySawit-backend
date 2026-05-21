package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilHistoryResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.mapper.HasilHistoryResponseMapper;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.specification.HasilSpecification;
import id.ac.ui.cs.advprog.mysawit.hasil.specification.HasilSpecifications;
import id.ac.ui.cs.advprog.mysawit.hasil.worker.HasilWorkerDirectory;

@Service
public class HasilHistoryQueryService {
    private final HasilService hasilService;
    private final HasilWorkerDirectory workerDirectory;
    private final HasilHistoryResponseMapper mapper;

    public HasilHistoryQueryService(
            HasilService hasilService,
            HasilWorkerDirectory workerDirectory,
            HasilHistoryResponseMapper mapper
    ) {
        this.hasilService = hasilService;
        this.workerDirectory = workerDirectory;
        this.mapper = mapper;
    }

    public List<HasilHistoryResponse> personalHistory(
            String workerId,
            LocalDate startDate,
            LocalDate endDate,
            HasilStatus status
    ) {
        HasilSpecification specification = HasilSpecifications.workerIs(workerId)
                .and(HasilSpecifications.dateRange(startDate, endDate))
                .and(HasilSpecifications.statusIs(status));
        return findMatching(specification);
    }

    public List<HasilHistoryResponse> mandorHistory(
            String mandorUsername,
            LocalDate date,
            String workerName
    ) {
        Set<String> supervisedWorkerIds = workerDirectory.findSupervisedWorkerIds(mandorUsername);
        HasilSpecification specification = HasilSpecifications.workerIn(supervisedWorkerIds)
                .and(HasilSpecifications.dateIs(date))
                .and(HasilSpecifications.workerNameContains(workerName, workerDirectory));
        return findMatching(specification);
    }

    public List<HasilHistoryResponse> workerHistory(
            String workerId,
            LocalDate startDate,
            LocalDate endDate,
            HasilStatus status
    ) {
        return personalHistory(workerId, startDate, endDate, status);
    }

    public List<HasilHistoryResponse> availableForPengiriman() {
        return findMatching(HasilSpecifications.visibleForPengiriman());
    }

    private List<HasilHistoryResponse> findMatching(HasilSpecification specification) {
        return hasilService.findAll().stream()
                .filter(specification::isSatisfiedBy)
                .sorted(historyComparator())
                .map(mapper::toResponse)
                .toList();
    }

    private Comparator<Hasil> historyComparator() {
        return Comparator.comparing(Hasil::getHasilDate).reversed().thenComparing(Hasil::getId);
    }
}
