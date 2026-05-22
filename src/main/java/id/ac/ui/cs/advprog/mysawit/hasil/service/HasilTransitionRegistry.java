package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

@Component
public class HasilTransitionRegistry {
    private final Map<String, HasilStatusTransitionStrategy> strategiesByAction;

    public HasilTransitionRegistry(List<HasilStatusTransitionStrategy> strategies) {
        this.strategiesByAction = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(HasilStatusTransitionStrategy::action, Function.identity()));
    }

    public HasilStatusTransitionStrategy get(String action) {
        HasilStatusTransitionStrategy strategy = strategiesByAction.get(action);
        if (strategy == null) {
            throw new IllegalArgumentException("unknown hasil transition action");
        }
        return strategy;
    }

    public static HasilTransitionRegistry defaultRegistry() {
        return new HasilTransitionRegistry(List.of(
                new ApproveHasilTransitionStrategy(),
                new RejectHasilTransitionStrategy()
        ));
    }
}

interface HasilStatusTransitionStrategy {
    String action();

    HasilTransitionResult apply(Hasil report, HasilTransitionRequest request);
}

record HasilTransitionRequest(String rejectionReason) {
    static HasilTransitionRequest empty() {
        return new HasilTransitionRequest(null);
    }
}

record HasilTransitionResult(Hasil report, boolean publishPayroll) {
}

@Component
class ApproveHasilTransitionStrategy implements HasilStatusTransitionStrategy {
    static final String ACTION_NAME = "approve";

    @Override
    public String action() {
        return ACTION_NAME;
    }

    @Override
    public HasilTransitionResult apply(Hasil report, HasilTransitionRequest request) {
        return new HasilTransitionResult(report.approveForPengiriman(), true);
    }
}

@Component
class RejectHasilTransitionStrategy implements HasilStatusTransitionStrategy {
    static final String ACTION_NAME = "reject";

    @Override
    public String action() {
        return ACTION_NAME;
    }

    @Override
    public HasilTransitionResult apply(Hasil report, HasilTransitionRequest request) {
        String rejectionReason = request == null ? null : request.rejectionReason();
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }
        return new HasilTransitionResult(report.reject(rejectionReason.trim()), false);
    }
}
