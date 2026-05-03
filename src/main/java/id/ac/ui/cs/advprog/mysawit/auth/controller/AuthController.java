package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterResponse;
import id.ac.ui.cs.advprog.mysawit.auth.service.AuthService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse resp = authService.register(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        Map<String, String> body = new HashMap<>();
        body.put("message", "hello");
        return ResponseEntity.ok(body);

    }
    
    @GetMapping("/hello/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> helloAdmin() {
        Map<String, String> body = new HashMap<>();
        body.put("message", "hello");
        return ResponseEntity.ok(body);

    }
}
