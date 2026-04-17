package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

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
                Hasil.of("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                Hasil.of("2", "buruh-2", LocalDate.of(2026, 3, 6), 80.0,
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
                Hasil.of("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                Hasil.of("2", "buruh-2", LocalDate.of(2026, 3, 7), 80.0,
                        "Panen lain", List.of("foto-2.jpg"), true, HasilStatus.SUBMITTED),
                Hasil.of("3", "buruh-3", LocalDate.of(2026, 3, 6), 70.0,
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
    void mandorCanViewAllSupervisedWorkersWhenNameFilterBlank() {
        setAuthentication("mandor-1", "MANDOR");
        given(hasilService.findAll()).willReturn(List.of(
                Hasil.of("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                Hasil.of("2", "buruh-2", LocalDate.of(2026, 3, 7), 80.0,
                        "Panen siang", List.of("foto-2.jpg"), true, HasilStatus.SUBMITTED)
        ));
        given(userRepository.findAll()).willReturn(List.of(
                new User("Budi", "buruh-1", "pw", Role.BURUH, null, "mandor-1"),
                new User("Beni", "buruh-2", "pw", Role.BURUH, null, "mandor-1")
        ));
        given(userRepository.findByUsername("buruh-1")).willReturn(java.util.Optional.of(
                new User(null, "buruh-1", "pw", Role.BURUH, null, "mandor-1")
        ));
        given(userRepository.findByUsername("buruh-2")).willReturn(java.util.Optional.of(
                new User("Beni", "buruh-2", "pw", Role.BURUH, null, "mandor-1")
        ));

        var response = controller.mandorHistory(null, "");

        assertEquals(200, response.getStatusCode().value());
        List<HasilHistoryResponse> body = response.getBody();
        assertEquals(2, body.size());
        assertEquals("buruh-1", body.get(1).workerName());
    }

    @Test
    void myHistoryRejectsInvalidDateRange() {
        setAuthentication("buruh-1", "BURUH");

        assertThrows(IllegalArgumentException.class,
                () -> controller.myHistory(
                        LocalDate.of(2026, 3, 31),
                        LocalDate.of(2026, 3, 1),
                        HasilStatus.SUBMITTED));
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

    @Test
    void buruhCanReportHarvestWhenNotYetSubmitted() {
        setAuthentication("buruh-1", "BURUH");
        given(hasilService.findByWorkerAndDate("buruh-1", LocalDate.now()))
                .willReturn(java.util.Optional.empty());

        var response = controller.todayStatus();

        assertEquals(false, response.hasSubmittedToday());
        assertEquals("Anda belum melaporkan panen hari ini", response.message());
    }

    @Test
    void buruhCannotSubmitWhenAlreadyReported() {
        setAuthentication("buruh-1", "BURUH");
        given(hasilService.findByWorkerAndDate("buruh-1", LocalDate.now()))
                .willReturn(java.util.Optional.of(
                        Hasil.of("1", "buruh-1", LocalDate.now(), 100.0,
                                "Panen", List.of("foto.jpg"), true, HasilStatus.SUBMITTED)
                ));

        var response = controller.todayStatus();

        assertEquals(true, response.hasSubmittedToday());
        assertEquals("Panen hari ini sudah dilaporkan dan tidak bisa diedit", response.message());
    }

    @Test
    void workerHistoryForMandorWithStatusFilter() {
        setAuthentication("mandor-1", "MANDOR");
        given(hasilService.findAll()).willReturn(List.of(
                Hasil.of("1", "buruh-1", LocalDate.of(2026, 3, 6), 100.0,
                        "Panen pagi", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED),
                Hasil.of("2", "buruh-1", LocalDate.of(2026, 3, 7), 80.0,
                        "Panen siang", List.of("foto-2.jpg"), true, HasilStatus.VERIFIED)
        ));
        given(userRepository.findByUsername("buruh-1")).willReturn(java.util.Optional.of(
                new User("Budi", "buruh-1", "pw", Role.BURUH, null, "mandor-1")
        ));

        var response = controller.workerHistoryForMandor(
                "buruh-1",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                HasilStatus.VERIFIED);

        assertEquals(200, response.getStatusCode().value());
        List<HasilHistoryResponse> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("VERIFIED", body.get(0).status());
    }

    @Test
    void buruhCanCreateHarvestReport() {
        setAuthentication("buruh-1", "BURUH");
        MultipartFile photo1 = org.mockito.Mockito.mock(MultipartFile.class);
        given(photo1.getOriginalFilename()).willReturn("foto-1.jpg");
        
        Hasil createdReport = Hasil.of("h-1", "buruh-1", LocalDate.now(), 125.5,
                "Panen dini", List.of("foto-1.jpg"), true, HasilStatus.SUBMITTED);
        given(hasilService.create(any(String.class), any(Double.class), any(String.class), any(List.class)))
                .willReturn(createdReport);

        var response = controller.create(125.5, "Panen dini", List.of(photo1));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        assertEquals("h-1", body.get("id"));
        assertEquals("buruh-1", body.get("workerId"));
        assertEquals("SUBMITTED", body.get("status"));
    }

    @Test
    void unauthenticatedUserCannotAccessHistory() {
        // No authentication set
        assertThrows(AccessDeniedException.class,
                () -> controller.myHistory(null, null, null));
    }

    @Test
    void unauthorizedRoleCannotAccessBuruhFeatures() {
        setAuthentication("user-1", "PEMBELI");
        assertThrows(AccessDeniedException.class,
                () -> controller.myHistory(null, null, null));
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