package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.util.List;

public class SupirAssignmentProfileResponse {
    private String username;
    private String email;
    private List<PengirimanAssignmentResponse> assignments;

    public SupirAssignmentProfileResponse() {
    }

    public SupirAssignmentProfileResponse(
            String username,
            String email,
            List<PengirimanAssignmentResponse> assignments) {
        this.username = username;
        this.email = email;
        this.assignments = assignments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PengirimanAssignmentResponse> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<PengirimanAssignmentResponse> assignments) {
        this.assignments = assignments;
    }
}
