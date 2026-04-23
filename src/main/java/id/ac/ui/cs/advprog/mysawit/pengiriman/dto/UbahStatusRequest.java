package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

import java.util.UUID;

public class UbahStatusRequest {
    private UUID supirTrukId;
    private StatusPengiriman statusBaru;

    public UbahStatusRequest() {
    }

    public UbahStatusRequest(UUID supirTrukId, StatusPengiriman statusBaru) {
        this.supirTrukId = supirTrukId;
        this.statusBaru = statusBaru;
    }

    public UUID getSupirTrukId() {
        return supirTrukId;
    }

    public void setSupirTrukId(UUID supirTrukId) {
        this.supirTrukId = supirTrukId;
    }

    public StatusPengiriman getStatusBaru() {
        return statusBaru;
    }

    public void setStatusBaru(StatusPengiriman statusBaru) {
        this.statusBaru = statusBaru;
    }
}
