package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class PartialRejectPengirimanRequest {
    private Long adminId;
    private double muatanKgDiakui;
    private String alasanPenolakan;

    public PartialRejectPengirimanRequest() {
    }

    public PartialRejectPengirimanRequest(Long adminId, double muatanKgDiakui, String alasanPenolakan) {
        this.adminId = adminId;
        this.muatanKgDiakui = muatanKgDiakui;
        this.alasanPenolakan = alasanPenolakan;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public double getMuatanKgDiakui() {
        return muatanKgDiakui;
    }

    public void setMuatanKgDiakui(double muatanKgDiakui) {
        this.muatanKgDiakui = muatanKgDiakui;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }
}
