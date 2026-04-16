package id.ac.ui.cs.advprog.mysawit.service;

import id.ac.ui.cs.advprog.mysawit.dto.AuthRequest;
import id.ac.ui.cs.advprog.mysawit.dto.AuthResponse;
import id.ac.ui.cs.advprog.mysawit.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    void register(RegisterRequest request);
}
