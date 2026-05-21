package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunUserReaderAdapterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private KebunUserReaderAdapter adapter;

    private User createUser(Long id, String fullname, String username, Role role, String cert) {
        return new User(id, fullname, username, "password", role, cert, null);
    }

    @Test
    void findUserById_found_shouldReturnSnapshot() {
        User user = createUser(10L, "Pak Mandor", "mandor1", Role.MANDOR, "CERT-001");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        Optional<UserSnapshot> result = adapter.findUserById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals("Pak Mandor", result.get().getFullname());
        assertEquals("mandor1", result.get().getUsername());
        assertEquals(Role.MANDOR, result.get().getRole());
        assertEquals("CERT-001", result.get().getCertificationNumber());
    }

    @Test
    void findUserById_notFound_shouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserSnapshot> result = adapter.findUserById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findUsersByRole_shouldFilterByRole() {
        User mandor = createUser(10L, "Mandor A", "mandor_a", Role.MANDOR, "CERT-001");
        User supir = createUser(20L, "Supir A", "supir_a", Role.SUPIR, null);
        when(userRepository.findAll()).thenReturn(List.of(mandor, supir));

        List<UserSnapshot> result = adapter.findUsersByRole("MANDOR");

        assertEquals(1, result.size());
        assertEquals("Mandor A", result.get(0).getFullname());
    }

    @Test
    void findUsersByRole_lowercaseInput_shouldWork() {
        User mandor = createUser(10L, "Mandor A", "mandor_a", Role.MANDOR, "CERT-001");
        when(userRepository.findAll()).thenReturn(List.of(mandor));

        List<UserSnapshot> result = adapter.findUsersByRole("mandor");

        assertEquals(1, result.size());
    }

    @Test
    void findUsersByIds_shouldReturnSnapshots() {
        User u1 = createUser(20L, "Supir A", "supir_a", Role.SUPIR, null);
        User u2 = createUser(21L, "Supir B", "supir_b", Role.SUPIR, null);
        when(userRepository.findAllById(List.of(20L, 21L))).thenReturn(List.of(u1, u2));

        List<UserSnapshot> result = adapter.findUsersByIds(List.of(20L, 21L));

        assertEquals(2, result.size());
        assertEquals("Supir A", result.get(0).getFullname());
        assertEquals("Supir B", result.get(1).getFullname());
    }

    @Test
    void findUsersByIds_emptyList_shouldReturnEmpty() {
        when(userRepository.findAllById(List.of())).thenReturn(List.of());

        List<UserSnapshot> result = adapter.findUsersByIds(List.of());
        assertTrue(result.isEmpty());
    }
}
