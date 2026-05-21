package id.ac.ui.cs.advprog.mysawit.hasil.service;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

@Component
public class ApproveHasilTransitionStrategy implements HasilStatusTransitionStrategy {
    public static final String ACTION = "approve";

    @Override
    public String action() {
        return ACTION;
    }

    @Override
    public HasilTransitionResult apply(Hasil report, HasilTransitionRequest request) {
        return new HasilTransitionResult(report.approveForPengiriman(), true);
    }
}
