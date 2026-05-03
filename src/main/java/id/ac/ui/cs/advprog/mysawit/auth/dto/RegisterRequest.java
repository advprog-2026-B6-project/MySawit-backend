package id.ac.ui.cs.advprog.mysawit.auth.dto;


import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String fullname;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Role role; 

    private String certificationNumber;
}
