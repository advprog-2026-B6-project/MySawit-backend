package id.ac.ui.cs.advprog.mysawit.dto;

import id.ac.ui.cs.advprog.mysawit.model.Role;

public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
    private String certificationNumber;

    public UserDto() {
    }

    public UserDto(Long id, String fullname, String username, Role role, String certificationNumber) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.role = role;
        this.certificationNumber = certificationNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }
}
