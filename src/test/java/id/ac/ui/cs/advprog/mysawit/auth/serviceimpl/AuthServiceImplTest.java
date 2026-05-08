package id.ac.ui.cs.advprog.mysawit.auth.serviceimpl;

import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterResponse;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_success_returnsToken() {
        LoginRequest req = new LoginRequest("jane", "pass");
        User user = new User(1L, "Jane Doe", "jane", "ENCODED", Role.BURUH, null, null);

        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "ENCODED")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("token-123");

        LoginResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("token-123", resp.getToken());
        verify(jwtUtil).generateToken(user);
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest req = new LoginRequest("john", "x");
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid"));
    }

    @Test
    void login_wrongPassword_throws() {
        LoginRequest req = new LoginRequest("john", "wrong");
        User user = new User(2L, "John Doe", "john", "ENCODED", Role.BURUH, null, null);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid"));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void register_adminRoleIsForcedToBuruh_andPasswordEncoded() {
        RegisterRequest req = new RegisterRequest("Admin User", "admin1", "plain", Role.ADMIN, null);
        when(userRepository.existsByUsername("admin1")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("ENC");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User toSave = inv.getArgument(0);
            return new User(100L, toSave.getFullname(), toSave.getUsername(), toSave.getPassword(),
                    toSave.getRole(), toSave.getCertificationNumber(), toSave.getMandorUsername());
        });

        RegisterResponse resp = authService.register(req);

        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(Role.BURUH, saved.getRole());
        assertEquals("ENC", saved.getPassword());

        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals("admin1", resp.getUsername());
        assertEquals(Role.BURUH, resp.getRole());
    }

    @Test
    void register_usernameExists_throws() {
        RegisterRequest req = new RegisterRequest("Jane", "jane", "pw", Role.BURUH, null);
        when(userRepository.existsByUsername("jane")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_mandorWithoutCertification_throws() {
        RegisterRequest req = new RegisterRequest("M", "m1", "pw", Role.MANDOR, null);
        when(userRepository.existsByUsername("m1")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void register_mandorWithBlankCertification_throws() {
        RegisterRequest req = new RegisterRequest("M", "m1", "pw", Role.MANDOR, "   ");
        when(userRepository.existsByUsername("m1")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void register_mandorWithCertification_succeeds() {
        RegisterRequest req = new RegisterRequest("M", "m1", "pw", Role.MANDOR, "CERT-1");
        when(userRepository.existsByUsername("m1")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("ENC-PW");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(2L, u.getFullname(), u.getUsername(), u.getPassword(), u.getRole(),
                    u.getCertificationNumber(), u.getMandorUsername());
        });

        RegisterResponse resp = authService.register(req);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(Role.MANDOR, saved.getRole());
        assertEquals("CERT-1", saved.getCertificationNumber());
        assertEquals("ENC-PW", saved.getPassword());
        assertEquals(2L, resp.getId());
    }

    @Test
    void register_buruhIgnoresCertification_setsNull() {
        RegisterRequest req = new RegisterRequest("B", "b1", "pw", Role.BURUH, "IGNORED-CERT");
        when(userRepository.existsByUsername("b1")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("ENC");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(3L, u.getFullname(), u.getUsername(), u.getPassword(), u.getRole(),
                    u.getCertificationNumber(), u.getMandorUsername());
        });

        authService.register(req);
        verify(userRepository).save(captor.capture());
        assertNull(captor.getValue().getCertificationNumber());
    }
}
