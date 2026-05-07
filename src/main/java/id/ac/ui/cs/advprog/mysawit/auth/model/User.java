package id.ac.ui.cs.advprog.mysawit.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String certificationNumber; // cmn utk MANDOR 

    private String mandorUsername;

    public User() {
    }

    public User(String fullname, String username, String password, Role role, String certificationNumber) {
        this(fullname, username, password, role, certificationNumber, null);
    }
    // just in case old code masih manggil constructor lama, biar ga error.
    // udah fully implemented? hapus yang atas, keep yang bawah

    public User(String fullname, String username, String password, Role role, String certificationNumber,
                String mandorUsername) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.role = role;
        this.certificationNumber = certificationNumber;
        this.mandorUsername = mandorUsername;
    }

    public Long getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMandorUsername() {
        return mandorUsername;
    }

    public void setMandorUsername(String mandorUsername) {
        this.mandorUsername = mandorUsername;
    }
}


