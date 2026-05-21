package id.ac.ui.cs.advprog.mysawit.hasil.transition;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

public interface HasilStatusTransitionStrategy {
    String action();

    HasilTransitionResult apply(Hasil report, HasilTransitionRequest request);
}
