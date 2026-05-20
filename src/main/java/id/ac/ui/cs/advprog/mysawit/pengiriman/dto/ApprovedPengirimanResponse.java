package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

public class ApprovedPengirimanResponse {
    private Long assignmentId;
    private UUID pengirimanId;
    private UUID supirTrukId;
    private Long mandorId;
    private String mandorName;
    private double muatanKg;
    private String tujuan;
    private LocalDateTime waktuDisetujui;
    private StatusPengiriman status;
    private ApprovalAssignment adminFinalApproval;
    private String adminFinalNote;
    private Double kilogramDiakui;
    private LocalDateTime adminFinalReviewedAt;

    public ApprovedPengirimanResponse() {
    }

    public ApprovedPengirimanResponse(Long assignmentId, UUID pengirimanId, UUID supirTrukId, Long mandorId,
                                      String mandorName, double muatanKg, String tujuan,
                                      LocalDateTime waktuDisetujui, StatusPengiriman status) {
        this.assignmentId = assignmentId;
        this.pengirimanId = pengirimanId;
        this.supirTrukId = supirTrukId;
        this.mandorId = mandorId;
        this.mandorName = mandorName;
        this.muatanKg = muatanKg;
        this.tujuan = tujuan;
        this.waktuDisetujui = waktuDisetujui;
        this.status = status;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public UUID getPengirimanId() {
        return pengirimanId;
    }

    public void setPengirimanId(UUID pengirimanId) {
        this.pengirimanId = pengirimanId;
    }

    public UUID getSupirTrukId() {
        return supirTrukId;
    }

    public void setSupirTrukId(UUID supirTrukId) {
        this.supirTrukId = supirTrukId;
    }

    public Long getMandorId() {
        return mandorId;
    }

    public void setMandorId(Long mandorId) {
        this.mandorId = mandorId;
    }

    public String getMandorName() {
        return mandorName;
    }

    public void setMandorName(String mandorName) {
        this.mandorName = mandorName;
    }

    public double getMuatanKg() {
        return muatanKg;
    }

    public void setMuatanKg(double muatanKg) {
        this.muatanKg = muatanKg;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public LocalDateTime getWaktuDisetujui() {
        return waktuDisetujui;
    }

    public void setWaktuDisetujui(LocalDateTime waktuDisetujui) {
        this.waktuDisetujui = waktuDisetujui;
    }

    public StatusPengiriman getStatus() {
        return status;
    }

    public void setStatus(StatusPengiriman status) {
        this.status = status;
    }

    public ApprovalAssignment getAdminFinalApproval() {
        return adminFinalApproval;
    }

    public void setAdminFinalApproval(ApprovalAssignment adminFinalApproval) {
        this.adminFinalApproval = adminFinalApproval;
    }

    public String getAdminFinalNote() {
        return adminFinalNote;
    }

    public void setAdminFinalNote(String adminFinalNote) {
        this.adminFinalNote = adminFinalNote;
    }

    public Double getKilogramDiakui() {
        return kilogramDiakui;
    }

    public void setKilogramDiakui(Double kilogramDiakui) {
        this.kilogramDiakui = kilogramDiakui;
    }

    public LocalDateTime getAdminFinalReviewedAt() {
        return adminFinalReviewedAt;
    }

    public void setAdminFinalReviewedAt(LocalDateTime adminFinalReviewedAt) {
        this.adminFinalReviewedAt = adminFinalReviewedAt;
    }
}
