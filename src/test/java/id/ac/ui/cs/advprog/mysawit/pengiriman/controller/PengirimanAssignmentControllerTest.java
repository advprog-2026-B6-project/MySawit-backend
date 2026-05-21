package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UpdateAssignmentApprovalRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UpdateAssignmentStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanAssignmentService;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PengirimanAssignmentControllerTest {

    @Mock
    private PengirimanAssignmentService assignmentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupirIdentityMapper supirIdentityMapper;

    @InjectMocks
    private PengirimanAssignmentController controller;

    private PengirimanAssignmentRequest request;
    private PengirimanAssignmentResponse response;

    @BeforeEach
    void setUp() {
        request = new PengirimanAssignmentRequest("mandor@mysawit.id", "supir@mysawit.id", 120.0, "Pabrik A");
        response = new PengirimanAssignmentResponse(
                1L,
                "mandor@mysawit.id",
                "supir@mysawit.id",
                120.0,
                "Pabrik A",
                StatusAssignment.MEMUAT,
                ApprovalAssignment.APPROVED,
                "OK",
                LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticatedUser(String username) {
        org.springframework.security.core.context.SecurityContext context = 
                org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new org.springframework.security.authentication.TestingAuthenticationToken(
                        username, null, "ROLE_USER"));
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }

    @Test
    void createAssignmentSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(assignmentService.createAssignment(any(PengirimanAssignmentRequest.class), eq("mandor@mysawit.id")))
                .thenReturn(response);

        ResponseEntity<?> result = controller.createAssignment(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void createAssignmentUnauthorized() {
        assertThrows(IllegalStateException.class, () -> controller.createAssignment(request));
    }

    @Test
    void createAssignmentBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(assignmentService.createAssignment(any(PengirimanAssignmentRequest.class), any()))
                .thenThrow(new IllegalArgumentException("invalid"));

        assertThrows(IllegalArgumentException.class, () -> controller.createAssignment(request));
    }

    @Test
    void getAllAssignmentsSuccess() {
        when(assignmentService.getAllAssignments()).thenReturn(List.of(response));
        ResponseEntity<?> result = controller.getAllAssignments();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAssignmentsMandorSayaSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(assignmentService.getAssignmentsByMandorEmail("mandor@mysawit.id")).thenReturn(List.of(response));
        ResponseEntity<?> result = controller.getAssignmentsMandorSaya();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAssignmentsMandorSayaUnauthorized() {
        assertThrows(IllegalStateException.class, controller::getAssignmentsMandorSaya);
    }

    @Test
    void getAssignmentsSupirSayaSuccess() {
        setAuthenticatedUser("supir@mysawit.id");
        when(assignmentService.getAssignmentsBySupirEmail("supir@mysawit.id")).thenReturn(List.of(response));
        ResponseEntity<?> result = controller.getAssignmentsSupirSaya();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirSayaUnauthorized() {
        assertThrows(IllegalStateException.class, controller::getAssignmentsSupirSaya);
    }

    @Test
    void getRiwayatAssignmentsSupirSayaSuccess() {
        setAuthenticatedUser("supir@mysawit.id");
        when(assignmentService.getRiwayatAssignmentsBySupirEmail(eq("supir@mysawit.id"), any(), any()))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = controller.getRiwayatAssignmentsSupirSaya(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getRiwayatAssignmentsSupirSayaBadRequest() {
        setAuthenticatedUser("supir@mysawit.id");
        when(assignmentService.getRiwayatAssignmentsBySupirEmail(eq("supir@mysawit.id"), any(), any()))
                .thenThrow(new IllegalArgumentException("invalid"));

        assertThrows(IllegalArgumentException.class, () -> controller.getRiwayatAssignmentsSupirSaya(
                LocalDate.of(2026, 5, 3),
                LocalDate.of(2026, 5, 1)));
    }

    @Test
    void getRiwayatAssignmentsSupirSayaUnauthorized() {
        assertThrows(IllegalStateException.class, () -> controller.getRiwayatAssignmentsSupirSaya(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2)));
    }

    @Test
    void getAssignmentsSupirByMandorSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        UUID supirId = UUID.nameUUIDFromBytes("supir@mysawit.id".getBytes());
        User supir = new User(1L, "Supir", "supir@mysawit.id", "pw", Role.SUPIR, null, null);
        when(userRepository.findAll()).thenReturn(List.of(supir));
        when(supirIdentityMapper.toSupirId("supir@mysawit.id")).thenReturn(supirId);
        when(assignmentService.getAssignmentsByMandorAndSupirEmail("mandor@mysawit.id", "supir@mysawit.id"))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = controller.getAssignmentsSupirByMandor(supirId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirByMandorBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(userRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> controller.getAssignmentsSupirByMandor(UUID.randomUUID()));
    }

    @Test
    void getAssignmentsSupirByMandorUnauthorized() {
        assertThrows(IllegalStateException.class, () -> controller.getAssignmentsSupirByMandor(UUID.randomUUID()));
    }

    @Test
    void getAssignmentsSupirByMandorBadRequestWhenUserNotSupir() {
        setAuthenticatedUser("mandor@mysawit.id");
        User mandor = new User(2L, "Mandor", "mandor2@mysawit.id", "pw", Role.MANDOR, null, null);
        when(userRepository.findAll()).thenReturn(List.of(mandor));

        assertThrows(IllegalArgumentException.class, () -> controller.getAssignmentsSupirByMandor(UUID.randomUUID()));
    }

    @Test
    void getSupirProfileByEmailForMandorSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        User supir = new User(1L, "Supir", "supir@mysawit.id", "pw", Role.SUPIR, null, null);
        when(userRepository.findByUsername("supir@mysawit.id")).thenReturn(Optional.of(supir));
        when(assignmentService.getAssignmentsByMandorAndSupirEmail("mandor@mysawit.id", "supir@mysawit.id"))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = controller.getSupirProfileByEmailForMandor("supir@mysawit.id");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getSupirProfileByEmailForMandorBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(userRepository.findByUsername("supir@mysawit.id")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> controller.getSupirProfileByEmailForMandor("supir@mysawit.id"));
    }

    @Test
    void getSupirProfileByEmailForMandorUnauthorized() {
        assertThrows(IllegalStateException.class, () -> controller.getSupirProfileByEmailForMandor("supir@mysawit.id"));
    }

    @Test
    void getSupirProfileByEmailForMandorBadRequestWhenRoleNotSupir() {
        setAuthenticatedUser("mandor@mysawit.id");
        User mandor = new User(2L, "Mandor", "supir@mysawit.id", "pw", Role.MANDOR, null, null);
        when(userRepository.findByUsername("supir@mysawit.id")).thenReturn(Optional.of(mandor));

        assertThrows(IllegalArgumentException.class,
                () -> controller.getSupirProfileByEmailForMandor("supir@mysawit.id"));
    }

    @Test
    void updateStatusSuccess() {
        setAuthenticatedUser("supir@mysawit.id");
        UpdateAssignmentStatusRequest update = new UpdateAssignmentStatusRequest(StatusAssignment.TIBA);
        when(assignmentService.updateStatus(1L, "supir@mysawit.id", StatusAssignment.TIBA)).thenReturn(response);

        ResponseEntity<?> result = controller.updateStatus(1L, update);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateStatusUnauthorized() {
        UpdateAssignmentStatusRequest update = new UpdateAssignmentStatusRequest(StatusAssignment.TIBA);
        assertThrows(IllegalStateException.class, () -> controller.updateStatus(1L, update));
    }

    @Test
    void updateStatusBadRequest() {
        setAuthenticatedUser("supir@mysawit.id");
        UpdateAssignmentStatusRequest update = new UpdateAssignmentStatusRequest(StatusAssignment.TIBA);
        when(assignmentService.updateStatus(1L, "supir@mysawit.id", StatusAssignment.TIBA))
                .thenThrow(new IllegalArgumentException("invalid"));
        assertThrows(IllegalArgumentException.class, () -> controller.updateStatus(1L, update));
    }

    @Test
    void updateApprovalSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        UpdateAssignmentApprovalRequest update = new UpdateAssignmentApprovalRequest(ApprovalAssignment.APPROVED, "ok");
        when(assignmentService.updateApproval(1L, "mandor@mysawit.id", ApprovalAssignment.APPROVED, "ok"))
                .thenReturn(response);

        ResponseEntity<?> result = controller.updateApproval(1L, update);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateApprovalBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        UpdateAssignmentApprovalRequest update = new UpdateAssignmentApprovalRequest(ApprovalAssignment.REJECTED, "no");
        when(assignmentService.updateApproval(1L, "mandor@mysawit.id", ApprovalAssignment.REJECTED, "no"))
                .thenThrow(new IllegalArgumentException("invalid"));

        assertThrows(IllegalArgumentException.class, () -> controller.updateApproval(1L, update));
    }

    @Test
    void updateApprovalUnauthorized() {
        UpdateAssignmentApprovalRequest update = new UpdateAssignmentApprovalRequest(ApprovalAssignment.APPROVED, "ok");
        assertThrows(IllegalStateException.class, () -> controller.updateApproval(1L, update));
    }

    @Test
    void getAssignmentsMandorSayaUnauthorizedForAnonymousUser() {
        setAuthenticatedUser("anonymousUser");
        assertThrows(IllegalStateException.class, controller::getAssignmentsMandorSaya);
    }
}
