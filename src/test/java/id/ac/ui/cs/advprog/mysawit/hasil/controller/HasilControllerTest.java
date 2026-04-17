package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilHistoryResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.service.HasilService;
import id.ac.ui.cs.advprog.mysawit.model.Role;
import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class HasilControllerTest {

    @Mock
    private HasilService hasilService;

    @Mock
    private UserRepository userRepository;

    private HasilController controller;

    @BeforeEach
    void setUp() {
        controller = new HasilController(hasilService, userRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void buruhCanSeeOwnHistoryWithFilters() {
        setAuthentication("buruh-1", "BURUH");
        given(hasilService.findAll()).willReturn(List.of(
                new Hasil("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                new Hasil("2", "buruh-2", LocalDate.of(2026, 3, 6), 80.0,
                        "Panen lain", List.of("foto-2.jpg"), true, HasilStatus.SUBMITTED)
        ));
        given(userRepository.findByUsername("buruh-1")).willReturn(java.util.Optional.of(
                new User("Buruh Satu", "buruh-1", "pw", Role.BURUH, null, "mandor-1")
        ));

        var response = controller.myHistory(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), HasilStatus.SUBMITTED);

        assertEquals(200, response.getStatusCode().value());
        List<HasilHistoryResponse> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("buruh-1", body.get(0).workerId());
        assertEquals("Buruh Satu", body.get(0).workerName());
        assertEquals("SUBMITTED", body.get(0).status());
    }

    @Test
    void mandorCanFilterHistoryByDateAndWorkerName() {
        setAuthentication("mandor-1", "MANDOR");
        given(hasilService.findAll()).willReturn(List.of(
                new Hasil("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                new Hasil("2", "buruh-2", LocalDate.of(2026, 3, 7), 80.0,
                        "Panen lain", List.of("foto-2.jpg"), true, HasilStatus.SUBMITTED),
                new Hasil("3", "buruh-3", LocalDate.of(2026, 3, 6), 70.0,
                        "Panen luar", List.of("foto-3.jpg"), true, HasilStatus.SUBMITTED)
        ));
        given(userRepository.findAll()).willReturn(List.of(
                new User("Budi", "buruh-1", "pw", Role.BURUH, null, "mandor-1"),
                new User("Beni", "buruh-2", "pw", Role.BURUH, null, "mandor-1"),
                new User("Rani", "buruh-3", "pw", Role.BURUH, null, "mandor-lain")
        ));
        given(userRepository.findByUsername("buruh-1")).willReturn(java.util.Optional.of(
                new User("Budi", "buruh-1", "pw", Role.BURUH, null, "mandor-1")
        ));

        var response = controller.mandorHistory(LocalDate.of(2026, 3, 6), "Bud");

        assertEquals(200, response.getStatusCode().value());
        List<HasilHistoryResponse> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("buruh-1", body.get(0).workerId());
        assertEquals("Budi", body.get(0).workerName());
    }

    @Test
    void mandorCannotOpenWorkerOutsideScope() {
        setAuthentication("mandor-1", "MANDOR");
        given(userRepository.findByUsername("buruh-9")).willReturn(java.util.Optional.of(
                new User("Luar", "buruh-9", "pw", Role.BURUH, null, "mandor-lain")
        ));

        assertThrows(AccessDeniedException.class,
                () -> controller.workerHistoryForMandor("buruh-9", null, null, null));
    }

    private void setAuthentication(String username, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                )
        );
    }
}