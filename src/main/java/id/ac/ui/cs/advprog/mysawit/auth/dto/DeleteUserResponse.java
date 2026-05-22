package id.ac.ui.cs.advprog.mysawit.auth.dto;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserResponse {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
    private String certificationNumber;
    private String mandorUsername;
    private String message;

    public DeleteUserResponse(UserDto user) {
        this.id = user.getId();
        this.fullname = user.getFullname();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.certificationNumber = user.getCertificationNumber();
        this.mandorUsername = user.getMandorUsername();
        this.message = "User deleted successfully.";
    }

    public DeleteUserResponse(String message) {
        this.message = message;
    }
}
