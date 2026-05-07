package id.ac.ui.cs.advprog.mysawit.auth.serviceimpl;

import id.ac.ui.cs.advprog.mysawit.auth.dto.AuthRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.auth.security.JwtUtil;
import id.ac.ui.cs.advprog.mysawit.auth.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Optional<User> u = userRepository.findByUsername(request.getUsername());
        if (u.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        User user = u.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        Role role = Role.BURUH;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                Role requested = Role.valueOf(request.getRole());

                if (requested == Role.ADMIN) {
                    role = Role.BURUH;
                } else {
                    role = requested;
                }
            } catch (Exception ex) {
                throw new RuntimeException("Invalid role");
            }
        }

        if (role == Role.MANDOR) {
            if (request.getCertificationNumber() == null || request.getCertificationNumber().isBlank()) {
                throw new RuntimeException("Mandor must provide certificationNumber");
            }
        }

        User user = new User(request.getFullname(), request.getUsername(),
            passwordEncoder.encode(request.getPassword()), role, request.getCertificationNumber(),
            request.getMandorUsername());
        userRepository.save(user);
    }
}
