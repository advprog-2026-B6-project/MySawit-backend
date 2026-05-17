package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;

public class PengirimanAssignmentResponse {
    private Long id;
    private String mandorEmail;
    private String supirEmail;
    private double muatanKg;
    private String tujuan;
    private StatusAssignment status;
    private ApprovalAssignment approval;
    private String note;
    private LocalDateTime createdAt;

    public PengirimanAssignmentResponse() {
    }

    public PengirimanAssignmentResponse(Long id, String mandorEmail, String supirEmail, double muatanKg, String tujuan,
            StatusAssignment status, ApprovalAssignment approval, String note, LocalDateTime createdAt) {
        this.id = id;
        this.mandorEmail = mandorEmail;
        this.supirEmail = supirEmail;
        this.muatanKg = muatanKg;
        this.tujuan = tujuan;
        this.status = status;
        this.approval = approval;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMandorEmail() {
        return mandorEmail;
    }

    public void setMandorEmail(String mandorEmail) {
        this.mandorEmail = mandorEmail;
    }

    public String getSupirEmail() {
        return supirEmail;
    }

    public void setSupirEmail(String supirEmail) {
        this.supirEmail = supirEmail;
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

    public StatusAssignment getStatus() {
        return status;
    }

    public void setStatus(StatusAssignment status) {
        this.status = status;
    }

    public ApprovalAssignment getApproval() {
        return approval;
    }

    public void setApproval(ApprovalAssignment approval) {
        this.approval = approval;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
