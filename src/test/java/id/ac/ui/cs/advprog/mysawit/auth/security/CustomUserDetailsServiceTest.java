package id.ac.ui.cs.advprog.mysawit.auth.security;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_found_mapsAuthorities() {
        User u = new User(1L, "A", "alice", "pw", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("alice");
        assertEquals("alice", details.getUsername());
        assertEquals("pw", details.getPassword());
        List<String> roles = details.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        assertTrue(roles.contains("ROLE_MANDOR"));
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x"));
    }
}
