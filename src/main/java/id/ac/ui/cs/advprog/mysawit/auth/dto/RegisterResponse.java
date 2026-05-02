package id.ac.ui.cs.advprog.mysawit.auth.dto;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
}
