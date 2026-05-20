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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PengirimanAssignmentControllerTest {

    @Mock
    private PengirimanAssignmentService assignmentService;

    @Mock
    private UserRepository userRepository;

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
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of()));
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
        ResponseEntity<?> result = controller.createAssignment(request);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void createAssignmentBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(assignmentService.createAssignment(any(PengirimanAssignmentRequest.class), any()))
                .thenThrow(new IllegalArgumentException("invalid"));

        ResponseEntity<?> result = controller.createAssignment(request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
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
        ResponseEntity<?> result = controller.getAssignmentsMandorSaya();
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
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
        ResponseEntity<?> result = controller.getAssignmentsSupirSaya();
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
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

        ResponseEntity<?> result = controller.getRiwayatAssignmentsSupirSaya(
                LocalDate.of(2026, 5, 3),
                LocalDate.of(2026, 5, 1));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getRiwayatAssignmentsSupirSayaUnauthorized() {
        ResponseEntity<?> result = controller.getRiwayatAssignmentsSupirSaya(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2));
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirByMandorSuccess() {
        setAuthenticatedUser("mandor@mysawit.id");
        UUID supirId = UUID.nameUUIDFromBytes("supir@mysawit.id".getBytes());
        User supir = new User(1L, "Supir", "supir@mysawit.id", "pw", Role.SUPIR, null, null);
        when(userRepository.findAll()).thenReturn(List.of(supir));
        when(assignmentService.getAssignmentsByMandorAndSupirEmail("mandor@mysawit.id", "supir@mysawit.id"))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = controller.getAssignmentsSupirByMandor(supirId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirByMandorBadRequest() {
        setAuthenticatedUser("mandor@mysawit.id");
        when(userRepository.findAll()).thenReturn(List.of());

        ResponseEntity<?> result = controller.getAssignmentsSupirByMandor(UUID.randomUUID());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirByMandorUnauthorized() {
        ResponseEntity<?> result = controller.getAssignmentsSupirByMandor(UUID.randomUUID());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void getAssignmentsSupirByMandorBadRequestWhenUserNotSupir() {
        setAuthenticatedUser("mandor@mysawit.id");
        User mandor = new User(2L, "Mandor", "mandor2@mysawit.id", "pw", Role.MANDOR, null, null);
        when(userRepository.findAll()).thenReturn(List.of(mandor));

        ResponseEntity<?> result = controller.getAssignmentsSupirByMandor(UUID.randomUUID());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
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
        ResponseEntity<?> result = controller.getSupirProfileByEmailForMandor("supir@mysawit.id");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getSupirProfileByEmailForMandorUnauthorized() {
        ResponseEntity<?> result = controller.getSupirProfileByEmailForMandor("supir@mysawit.id");
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void getSupirProfileByEmailForMandorBadRequestWhenRoleNotSupir() {
        setAuthenticatedUser("mandor@mysawit.id");
        User mandor = new User(2L, "Mandor", "supir@mysawit.id", "pw", Role.MANDOR, null, null);
        when(userRepository.findByUsername("supir@mysawit.id")).thenReturn(Optional.of(mandor));

        ResponseEntity<?> result = controller.getSupirProfileByEmailForMandor("supir@mysawit.id");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
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
        ResponseEntity<?> result = controller.updateStatus(1L, update);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void updateStatusBadRequest() {
        setAuthenticatedUser("supir@mysawit.id");
        UpdateAssignmentStatusRequest update = new UpdateAssignmentStatusRequest(StatusAssignment.TIBA);
        when(assignmentService.updateStatus(1L, "supir@mysawit.id", StatusAssignment.TIBA))
                .thenThrow(new IllegalArgumentException("invalid"));
        ResponseEntity<?> result = controller.updateStatus(1L, update);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
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

        ResponseEntity<?> result = controller.updateApproval(1L, update);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void updateApprovalUnauthorized() {
        UpdateAssignmentApprovalRequest update = new UpdateAssignmentApprovalRequest(ApprovalAssignment.APPROVED, "ok");
        ResponseEntity<?> result = controller.updateApproval(1L, update);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void getAssignmentsMandorSayaUnauthorizedForAnonymousUser() {
        setAuthenticatedUser("anonymousUser");
        ResponseEntity<?> result = controller.getAssignmentsMandorSaya();
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }
}
