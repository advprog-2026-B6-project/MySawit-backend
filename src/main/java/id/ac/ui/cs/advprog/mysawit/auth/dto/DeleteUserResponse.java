package id.ac.ui.cs.advprog.mysawit.auth.dto;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
        UserDto deletedUser = Objects.requireNonNull(user);
        this.id = deletedUser.getId();
        this.fullname = deletedUser.getFullname();
        this.username = deletedUser.getUsername();
        this.role = deletedUser.getRole();
        this.certificationNumber = deletedUser.getCertificationNumber();
        this.mandorUsername = deletedUser.getMandorUsername();
        this.message = "User deleted successfully.";
    }

    public DeleteUserResponse(String message) {
        this.message = message;
    }
}
