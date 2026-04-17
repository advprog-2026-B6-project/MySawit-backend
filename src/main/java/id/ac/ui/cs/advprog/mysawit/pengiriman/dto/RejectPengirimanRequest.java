package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class RejectPengirimanRequest {
    private Long mandorId;
    private String alasanPenolakan;

    public RejectPengirimanRequest() {
    }

    public RejectPengirimanRequest(Long mandorId, String alasanPenolakan) {
        this.mandorId = mandorId;
        this.alasanPenolakan = alasanPenolakan;
    }

    public Long getMandorId() {
        return mandorId;
    }

    public void setMandorId(Long mandorId) {
        this.mandorId = mandorId;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }
}