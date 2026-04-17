package id.ac.ui.cs.advprog.mysawit.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String fullname;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String role; 
    private String certificationNumber;
    private String mandorUsername;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }

    public String getMandorUsername() {
        return mandorUsername;
    }

    public void setMandorUsername(String mandorUsername) {
        this.mandorUsername = mandorUsername;
    }
}
