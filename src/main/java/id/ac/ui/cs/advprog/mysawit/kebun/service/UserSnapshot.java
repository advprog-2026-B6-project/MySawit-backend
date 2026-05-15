package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSnapshot {
    private Long id;
    private String fullname;
    private String username;
    private Role role;
    private String certificationNumber;
}
