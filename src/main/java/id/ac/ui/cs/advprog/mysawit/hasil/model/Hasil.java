package id.ac.ui.cs.advprog.mysawit.hasil.model;

import java.time.LocalDate;
import java.util.List;

public class Hasil {
    private final String id;
    private final String workerId;
    private final LocalDate hasilDate;
    private final double weightKg;
    private final String news;
    private final List<String> photoUrls;
    private final boolean locked;
    private final HasilStatus status;

    public Hasil(String id, String workerId, LocalDate hasilDate, double weightKg,
                 String news, List<String> photoUrls, boolean locked, HasilStatus status) {
        this.id = id;
        this.workerId = workerId;
        this.hasilDate = hasilDate;
        this.weightKg = weightKg;
        this.news = news;
        this.photoUrls = List.copyOf(photoUrls);
        this.locked = locked;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public LocalDate getHasilDate() {
        return hasilDate;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getNews() {
        return news;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public boolean isLocked() {
        return locked;
    }

    public HasilStatus getStatus() {
        return status;
    }
}


