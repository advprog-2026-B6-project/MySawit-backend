package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.util.UUID;

public class BuatPengirimanRequest {
    private Long mandorId;
    private UUID supirTrukId;
    private double muatanKg;
    private String tujuan;

    public BuatPengirimanRequest() {
    }

    public BuatPengirimanRequest(Long mandorId, UUID supirTrukId, double muatanKg, String tujuan) {
        this.mandorId = mandorId;
        this.supirTrukId = supirTrukId;
        this.muatanKg = muatanKg;
        this.tujuan = tujuan;
    }

    public Long getMandorId() {
        return mandorId;
    }

    public void setMandorId(Long mandorId) {
        this.mandorId = mandorId;
    }

    public UUID getSupirTrukId() {
        return supirTrukId;
    }

    public void setSupirTrukId(UUID supirTrukId) {
        this.supirTrukId = supirTrukId;
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
}
