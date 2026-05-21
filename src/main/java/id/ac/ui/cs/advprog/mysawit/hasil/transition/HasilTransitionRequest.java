package id.ac.ui.cs.advprog.mysawit.hasil.transition;

public record HasilTransitionRequest(String rejectionReason) {
    public static HasilTransitionRequest empty() {
        return new HasilTransitionRequest(null);
    }
}
