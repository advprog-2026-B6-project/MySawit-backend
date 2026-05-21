package id.ac.ui.cs.advprog.mysawit.hasil.transition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

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
