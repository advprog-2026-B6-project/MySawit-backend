package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

public class PengirimanAssignmentRequest {
    private String mandorEmail;
    private String supirEmail;
    private double muatanKg;
    private String tujuan;

    public PengirimanAssignmentRequest() {
    }

    public PengirimanAssignmentRequest(String mandorEmail, String supirEmail, double muatanKg, String tujuan) {
        this.mandorEmail = mandorEmail;
        this.supirEmail = supirEmail;
        this.muatanKg = muatanKg;
        this.tujuan = tujuan;
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
}
