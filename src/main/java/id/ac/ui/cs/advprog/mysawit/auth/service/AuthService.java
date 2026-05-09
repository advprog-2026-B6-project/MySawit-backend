package id.ac.ui.cs.advprog.mysawit.auth.service;

import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);
}
