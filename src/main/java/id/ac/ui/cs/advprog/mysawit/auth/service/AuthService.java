package id.ac.ui.cs.advprog.mysawit.auth.service;

import id.ac.ui.cs.advprog.mysawit.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    void register(RegisterRequest request);
}
