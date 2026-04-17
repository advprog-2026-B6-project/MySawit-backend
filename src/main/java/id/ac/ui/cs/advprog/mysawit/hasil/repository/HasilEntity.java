package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;

@Entity
@Table(name = "hasil_reports")
public class HasilEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String workerId;

    @Column(nullable = false)
    private LocalDate hasilDate;

    @Column(nullable = false)
    private double weightKg;

    @Column(nullable = false, length = 2000)
    private String news;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hasil_report_photos", joinColumns = @JoinColumn(name = "hasil_report_id"))
    @Column(name = "photo_url", nullable = false)
    private List<String> photoUrls = new ArrayList<>();

    @Column(nullable = false)
    private boolean locked;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HasilStatus status;

    public HasilEntity() {
        // constructor kosong buat JPA/Hibernate supaya bisa 
        // bikin entity ini saat ambil data dari db
    }

    public static HasilEntity from(Hasil report) {
        HasilEntity entity = new HasilEntity();
        entity.id = report.getId();
        entity.workerId = report.getWorkerId();
        entity.hasilDate = report.getHasilDate();
        entity.weightKg = report.getWeightKg();
        entity.news = report.getNews();
        entity.photoUrls = new ArrayList<>(report.getPhotoUrls());
        entity.locked = report.isLocked();
        entity.status = report.getStatus();
        return entity;
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
