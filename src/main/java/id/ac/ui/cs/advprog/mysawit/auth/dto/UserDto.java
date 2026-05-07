package id.ac.ui.cs.advprog.mysawit.auth.dto;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
    private String certificationNumber;
    private String mandorUsername;

    public UserDto(User user) {
        this.id = user.getId();
        this.fullname = user.getFullname();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.certificationNumber = user.getCertificationNumber();
        this.mandorUsername = user.getMandorUsername();
    }
}
