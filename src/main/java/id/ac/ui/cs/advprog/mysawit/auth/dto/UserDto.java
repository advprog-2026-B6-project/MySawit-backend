package id.ac.ui.cs.advprog.mysawit.auth.dto;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
    private String certificationNumber;

    public UserDto(Long id, String fullname, String username, Role role, String certificationNumber) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.role = role;
        this.certificationNumber = certificationNumber;
    }
}
