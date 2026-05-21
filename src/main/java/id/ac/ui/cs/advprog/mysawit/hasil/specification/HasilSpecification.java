package id.ac.ui.cs.advprog.mysawit.hasil.specification;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

@FunctionalInterface
public interface HasilSpecification {
    boolean isSatisfiedBy(Hasil report);

    default HasilSpecification and(HasilSpecification other) {
        return report -> isSatisfiedBy(report) && other.isSatisfiedBy(report);
    }

    static HasilSpecification all() {
        return report -> true;
    }
}
