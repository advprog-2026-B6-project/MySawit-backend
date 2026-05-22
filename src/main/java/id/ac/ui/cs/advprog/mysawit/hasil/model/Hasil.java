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
                data.status(),
                data.rejectionReason(),
                data.visibleForPengiriman()
        );
    }

    @SuppressWarnings("java:S107")
    public static Hasil of(String id, String workerId, java.time.LocalDate hasilDate, double weightKg,
                           String news, List<String> photoUrls, boolean locked, HasilStatus status) {
        return of(id, workerId, hasilDate, weightKg, news, photoUrls, locked, status, null, false);
    }

    @SuppressWarnings("java:S107")
    public static Hasil of(String id, String workerId, java.time.LocalDate hasilDate, double weightKg,
                           String news, List<String> photoUrls, boolean locked, HasilStatus status,
                           String rejectionReason, boolean visibleForPengiriman) {
        return new Hasil(new HasilData(id, workerId, hasilDate, weightKg, news, photoUrls, locked, status,
                rejectionReason, visibleForPengiriman));
    }

    public Hasil approveForPengiriman() {
        return of(getId(), getWorkerId(), getHasilDate(), getWeightKg(), getNews(), getPhotoUrls(),
                isLocked(), HasilStatus.VERIFIED, null, true);
    }

    public Hasil reject(String rejectionReason) {
        return of(getId(), getWorkerId(), getHasilDate(), getWeightKg(), getNews(), getPhotoUrls(),
                isLocked(), HasilStatus.REJECTED, rejectionReason, false);
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

    public String getRejectionReason() {
        return data.rejectionReason();
    }

    public boolean isVisibleForPengiriman() {
        return data.visibleForPengiriman();
    }
}

