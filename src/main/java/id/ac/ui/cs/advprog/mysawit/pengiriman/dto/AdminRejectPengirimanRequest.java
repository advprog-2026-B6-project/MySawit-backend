package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class AdminRejectPengirimanRequest {
    private Long adminId;
    private String alasanPenolakan;

    public AdminRejectPengirimanRequest() {
    }

    public AdminRejectPengirimanRequest(Long adminId, String alasanPenolakan) {
        this.adminId = adminId;
        this.alasanPenolakan = alasanPenolakan;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }
}
