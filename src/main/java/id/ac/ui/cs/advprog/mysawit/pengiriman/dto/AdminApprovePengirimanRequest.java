package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class AdminApprovePengirimanRequest {
    private Long adminId;

    public AdminApprovePengirimanRequest() {
    }

    public AdminApprovePengirimanRequest(Long adminId) {
        this.adminId = adminId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
