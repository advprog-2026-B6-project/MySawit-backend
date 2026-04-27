package id.ac.ui.cs.advprog.mysawit.auth.dto;

import jakarta.validation.constraints.NotBlank;
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
    private String role; 
    private String certificationNumber;
    private String mandorUsername;
}
