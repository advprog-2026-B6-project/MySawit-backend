package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "pengiriman_assignments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PengirimanAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mandorEmail;

    @Column(nullable = false)
    private String supirEmail;

    @Column(nullable = false)
    private double muatanKg;

    @Column(nullable = false)
    private String tujuan;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false, columnDefinition = "varchar(32) default 'MEMUAT'")
    @Builder.Default
    private StatusAssignment status = StatusAssignment.MEMUAT;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_approval")
    private ApprovalAssignment approval;

    @Column(name = "approval_note", columnDefinition = "TEXT")
    private String note;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
