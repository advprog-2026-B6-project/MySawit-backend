package id.ac.ui.cs.advprog.mysawit.auth.security;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateAndValidateToken_roundtrip() {
        JwtUtil jwt = new JwtUtil("0123456789abcdef0123456789abcdef", 60_000L);
        User u = new User(1L, "A", "alice", "p", Role.ADMIN, null, null);
        String token = jwt.generateToken(u);
        assertNotNull(token);
        assertTrue(jwt.validateToken(token));
        assertEquals("alice", jwt.getUsername(token));
    }

    @Test
    void validateToken_tampered_returnsFalse() {
        JwtUtil jwt = new JwtUtil("0123456789abcdef0123456789abcdef", 60_000L);
        User u = new User(1L, "A", "alice", "p", Role.ADMIN, null, null);
        String token = jwt.generateToken(u);
        String bad = token + "x";
        assertFalse(jwt.validateToken(bad));
    }
}
