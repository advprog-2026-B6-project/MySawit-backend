package id.ac.ui.cs.advprog.mysawit.hasil.service;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

@Component
public class RejectHasilTransitionStrategy implements HasilStatusTransitionStrategy {
    public static final String ACTION_NAME = "reject";

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
