package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.DeleteUserResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private AdminController controller;

    @Test
    void ping_returnsPong() {
        ResponseEntity<String> out = controller.ping();
        assertEquals(200, out.getStatusCode().value());
        assertEquals("pong admin!", out.getBody());
    }

    @Test
    void getAllUsers_returnsList() {
        List<UserDto> list = List.of(new UserDto(1L, "A", "a", Role.BURUH, null, null));
        when(userService.getAllUsers()).thenReturn(list);
        ResponseEntity<List<UserDto>> out = controller.getAllUsers();
        assertEquals(200, out.getStatusCode().value());
        assertEquals(1, out.getBody().size());
    }

    @Test
    void deleteUser_found_returnsOk() {
        UserDto dto = new UserDto(1L, "A", "a", Role.BURUH, null, null);
        when(userService.deleteUserById(1L)).thenReturn(Optional.of(dto));
        ResponseEntity<DeleteUserResponse> out = controller.deleteUser(1L);
        assertEquals(200, out.getStatusCode().value());
        assertEquals(1L, out.getBody().getId());
    }

    @Test
    void deleteUser_notFound_returns404() {
        when(userService.deleteUserById(2L)).thenReturn(Optional.empty());
        ResponseEntity<DeleteUserResponse> out = controller.deleteUser(2L);
        assertEquals(404, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void deleteUser_illegalState_returns409() {
        when(userService.deleteUserById(3L))
                .thenThrow(new IllegalStateException("We cant delete that users as its been assigned to a Buruh"));
        ResponseEntity<DeleteUserResponse> out = controller.deleteUser(3L);
        assertEquals(409, out.getStatusCode().value());
        assertTrue(out.getBody().getMessage().contains("assigned to a Buruh"));
    }

    @Test
    void assignBuruhToMandor_success_returnsOk() {
        UserDto dto = new UserDto(1L, "B", "b", Role.BURUH, null, "m");
        when(userService.assignBuruhToMandor("b", "m")).thenReturn(Optional.of(dto));
        ResponseEntity<UserDto> out = controller.assignBuruhToMandor("b", "m");
        assertEquals(200, out.getStatusCode().value());
    }

    @Test
    void assignBuruhToMandor_notFound_returns404() {
        when(userService.assignBuruhToMandor("b", "m")).thenReturn(Optional.empty());
        ResponseEntity<UserDto> out = controller.assignBuruhToMandor("b", "m");
        assertEquals(404, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void assignBuruhToMandor_illegalArgument_returns400() {
        when(userService.assignBuruhToMandor("b", "x")).thenThrow(new IllegalArgumentException("bad"));
        ResponseEntity<UserDto> out = controller.assignBuruhToMandor("b", "x");
        assertEquals(400, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void assignBuruhToMandor_illegalState_returns409() {
        when(userService.assignBuruhToMandor("b", "m")).thenThrow(new IllegalStateException("conflict"));
        ResponseEntity<UserDto> out = controller.assignBuruhToMandor("b", "m");
        assertEquals(409, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void reassignBuruh_success_returnsOk() {
        UserDto dto = new UserDto(1L, "B", "b", Role.BURUH, null, "m2");
        when(userService.reassignBuruhToMandor("b", "m2")).thenReturn(Optional.of(dto));
        ResponseEntity<UserDto> out = controller.reassignBuruh("b", "m2");
        assertEquals(200, out.getStatusCode().value());
    }

    @Test
    void reassignBuruh_notFound_returns404() {
        when(userService.reassignBuruhToMandor("b", "m2")).thenReturn(Optional.empty());
        ResponseEntity<UserDto> out = controller.reassignBuruh("b", "m2");
        assertEquals(404, out.getStatusCode().value());
        assertNull(out.getBody());
    }

    @Test
    void reassignBuruh_illegalArgument_returns400() {
        when(userService.reassignBuruhToMandor("b", "x")).thenThrow(new IllegalArgumentException("bad"));
        ResponseEntity<UserDto> out = controller.reassignBuruh("b", "x");
        assertEquals(400, out.getStatusCode().value());
        assertNull(out.getBody());
    }
}
