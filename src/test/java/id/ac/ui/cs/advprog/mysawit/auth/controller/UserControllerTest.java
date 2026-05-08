package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void getUserById_found_returnsOk() {
        UserDto dto = new UserDto(1L, "A", "a", Role.BURUH, null, null);
        when(userService.getUserById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<UserDto> out = controller.getUserById(1L);
        assertEquals(200, out.getStatusCode().value());
        assertEquals("a", out.getBody().getUsername());
    }

    @Test
    void getUserById_notFound_returns404() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<UserDto> out = controller.getUserById(1L);
        assertEquals(404, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void example_returnsFormattedString() {
        UserDetails auth = User.withUsername("alice").password("p").roles("BURUH").build();
        UserDto dto = new UserDto(1L, "A", "alice", Role.BURUH, null, null);
        when(userService.getUserByUsername("alice")).thenReturn(Optional.of(dto));

        ResponseEntity<Object> out = controller.example(auth);
        assertEquals(200, out.getStatusCode().value());
        assertTrue(out.getBody().toString().contains("alice adalah BURUH"));
    }

    @Test
    void example_userNotFound_throws404() {
        UserDetails auth = User.withUsername("ghost").password("p").roles("BURUH").build();
        when(userService.getUserByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> controller.example(auth));
    }
}
