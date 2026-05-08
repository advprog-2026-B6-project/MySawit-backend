package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.*;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    void register_returnsOkWithBody() {
        RegisterRequest req = new RegisterRequest("A", "a", "p", Role.BURUH, null);
        RegisterResponse resp = new RegisterResponse(1L, "A", "a", Role.BURUH);
        when(authService.register(req)).thenReturn(resp);

        ResponseEntity<RegisterResponse> out = controller.register(req);
        assertEquals(200, out.getStatusCode().value());
        assertEquals(resp, out.getBody());
    }

    @Test
    void login_returnsOkWithBody() {
        LoginRequest req = new LoginRequest("a", "p");
        LoginResponse resp = new LoginResponse("tok");
        when(authService.login(req)).thenReturn(resp);
        ResponseEntity<LoginResponse> out = controller.login(req);
        assertEquals(200, out.getStatusCode().value());
        assertEquals("tok", out.getBody().getToken());
    }

    @Test
    void hello_returnsMessage() {
        ResponseEntity<Map<String, String>> out = controller.hello();
        assertEquals(200, out.getStatusCode().value());
        assertEquals("hello", out.getBody().get("message"));
    }

    @Test
    void helloAdmin_returnsMessage() {
        ResponseEntity<Map<String, String>> out = controller.helloAdmin();
        assertEquals(200, out.getStatusCode().value());
        assertEquals("hello", out.getBody().get("message"));
    }
}
