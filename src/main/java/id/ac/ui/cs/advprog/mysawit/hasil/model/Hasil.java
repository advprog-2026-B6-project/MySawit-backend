package id.ac.ui.cs.advprog.mysawit.hasil.model;

import java.util.List;

public class Hasil {
    private final HasilData data;

    public Hasil(HasilData data) {
        this.data = new HasilData(
                data.id(),
                data.workerId(),
                data.hasilDate(),
                data.weightKg(),
                data.news(),
                List.copyOf(data.photoUrls()),
                data.locked(),
                data.status()
        );
    }

    public static Hasil of(String id, String workerId, java.time.LocalDate hasilDate, double weightKg,
                           String news, List<String> photoUrls, boolean locked, HasilStatus status) {
        return new Hasil(new HasilData(id, workerId, hasilDate, weightKg, news, photoUrls, locked, status));
    }

    public String getId() {
        return data.id();
    }

    public String getWorkerId() {
        return data.workerId();
    }

    public java.time.LocalDate getHasilDate() {
        return data.hasilDate();
    }

    public double getWeightKg() {
        return data.weightKg();
    }

    public String getNews() {
        return data.news();
    }

    public List<String> getPhotoUrls() {
        return data.photoUrls();
    }

    public boolean isLocked() {
        return data.locked();
    }

    public HasilStatus getStatus() {
        return data.status();
    }
}


