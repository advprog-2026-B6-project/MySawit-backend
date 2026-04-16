package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class ApprovePengirimanRequest {
    private Long mandorId;

    public ApprovePengirimanRequest() {
    }

    public ApprovePengirimanRequest(Long mandorId) {
        this.mandorId = mandorId;
    }

    public Long getMandorId() {
        return mandorId;
    }

    public void setMandorId(Long mandorId) {
        this.mandorId = mandorId;
    }
}