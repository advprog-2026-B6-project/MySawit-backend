package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PengirimanAssignmentServiceImplTest {

    @Mock
    private PengirimanAssignmentRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PayrollRequestSender payrollRequestSender;

    @InjectMocks
    private PengirimanAssignmentServiceImpl service;

    private PengirimanAssignmentRequest request;

    @BeforeEach
    void setUp() {
        request = new PengirimanAssignmentRequest("mandor@mysawit.id", "supir@mysawit.id", 250.0, "Pabrik A");
    }

    @Test
    void createAssignment_success() {
        PengirimanAssignment saved = PengirimanAssignment.builder()
                .id(1L)
                .mandorEmail("mandor@mysawit.id")
                .supirEmail("supir@mysawit.id")
                .muatanKg(250.0)
                .tujuan("Pabrik A")
                .build();

        when(repository.save(any(PengirimanAssignment.class))).thenReturn(saved);

        var response = service.createAssignment(request, "mandor@mysawit.id");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("mandor@mysawit.id", response.getMandorEmail());
    }

    @Test
    void createAssignment_invalidRequest() {
        PengirimanAssignmentRequest badRequest = new PengirimanAssignmentRequest("", "supir@mysawit.id", 0, "");
        assertThrows(IllegalArgumentException.class, () -> service.createAssignment(badRequest, "mandor@mysawit.id"));
    }

    @Test
    void getAllAssignments_returnsList() {
        when(repository.findAll()).thenReturn(List.of());
        assertNotNull(service.getAllAssignments());
    }

    @Test
    void getAssignmentsByMandorEmail_returnsList() {
        when(repository.findByMandorEmail("mandor@mysawit.id"))
                .thenReturn(List.of(PengirimanAssignment.builder().id(1L).build()));

        assertEquals(1, service.getAssignmentsByMandorEmail("mandor@mysawit.id").size());
    }

    @Test
    void getAssignmentsBySupirEmail_returnsList() {
        when(repository.findBySupirEmail("supir@mysawit.id"))
                .thenReturn(List.of(PengirimanAssignment.builder().id(2L).build()));

        assertEquals(1, service.getAssignmentsBySupirEmail("supir@mysawit.id").size());
    }

    @Test
    void getRiwayatAssignmentsBySupirEmail_filtersByDate() {
        PengirimanAssignment assignment = PengirimanAssignment.builder()
                .id(3L)
                .supirEmail("supir@mysawit.id")
                .status(StatusAssignment.TIBA)
                .createdAt(java.time.LocalDateTime.of(2026, 5, 2, 9, 0))
                .build();

        when(repository.findBySupirEmail("supir@mysawit.id")).thenReturn(List.of(assignment));

        List<?> result = service.getRiwayatAssignmentsBySupirEmail(
                "supir@mysawit.id", java.time.LocalDate.of(2026, 5, 1), java.time.LocalDate.of(2026, 5, 3));

        assertEquals(1, result.size());
    }

    @Test
    void getRiwayatAssignmentsBySupirEmail_invalidRange() {
        assertThrows(IllegalArgumentException.class, () ->
                service.getRiwayatAssignmentsBySupirEmail(
                        "supir@mysawit.id",
                        java.time.LocalDate.of(2026, 5, 3),
                        java.time.LocalDate.of(2026, 5, 1)));
    }

    @Test
    void updateStatus_invalidUser_throws() {
        PengirimanAssignment assignment = PengirimanAssignment.builder()
                .id(1L)
                .mandorEmail("mandor@mysawit.id")
                .supirEmail("supir@mysawit.id")
                .muatanKg(100)
                .tujuan("Pabrik")
                .status(StatusAssignment.MEMUAT)
                .build();
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(assignment));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(1L, "lain@mysawit.id", StatusAssignment.MENGIRIM));
    }

    @Test
    void updateStatus_success() {
        PengirimanAssignment assignment = PengirimanAssignment.builder()
                .id(2L)
                .mandorEmail("mandor@mysawit.id")
                .supirEmail("supir@mysawit.id")
                .muatanKg(100)
                .tujuan("Pabrik")
                .status(StatusAssignment.MEMUAT)
                .build();
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(assignment));
        when(repository.save(any(PengirimanAssignment.class))).thenAnswer(i -> i.getArguments()[0]);

        var response = service.updateStatus(2L, "supir@mysawit.id", StatusAssignment.MENGIRIM);

        assertEquals(StatusAssignment.MENGIRIM, response.getStatus());
    }

    @Test
    void updateStatus_nullStatus_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateStatus(1L, "supir@mysawit.id", null));
    }

    @Test
    void updateApproval_rejectWithoutNote_throws() {
        PengirimanAssignment assignment = PengirimanAssignment.builder()
                .id(1L)
                .mandorEmail("mandor@mysawit.id")
                .supirEmail("supir@mysawit.id")
                .muatanKg(100)
                .tujuan("Pabrik")
                .status(StatusAssignment.TIBA)
                .build();
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(assignment));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateApproval(1L, "mandor@mysawit.id", ApprovalAssignment.REJECTED, " "));
    }

    @Test
    void updateApproval_approved_sendsPayroll() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(2L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(100)
        .tujuan("Pabrik")
        .status(StatusAssignment.TIBA)
        .build();
    when(repository.findById(2L)).thenReturn(java.util.Optional.of(assignment));
    when(repository.save(any(PengirimanAssignment.class))).thenAnswer(i -> i.getArguments()[0]);
    when(userRepository.findByUsername("mandor@mysawit.id"))
        .thenReturn(java.util.Optional.of(new User("Mandor", "mandor", "secret", Role.MANDOR, null)));

    var response = service.updateApproval(2L, "mandor@mysawit.id", ApprovalAssignment.APPROVED, null);

    assertEquals(ApprovalAssignment.APPROVED, response.getApproval());
    org.mockito.Mockito.verify(payrollRequestSender)
        .sendPayrollRequest(any(id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest.class));
    }

    @Test
    void updateApproval_rejected_setsNote() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(3L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(100)
        .tujuan("Pabrik")
        .status(StatusAssignment.TIBA)
        .build();
    when(repository.findById(3L)).thenReturn(java.util.Optional.of(assignment));
    when(repository.save(any(PengirimanAssignment.class))).thenAnswer(i -> i.getArguments()[0]);

    var response = service.updateApproval(3L, "mandor@mysawit.id", ApprovalAssignment.REJECTED, "  Ditolak  ");

    assertEquals(ApprovalAssignment.REJECTED, response.getApproval());
    assertEquals("Ditolak", response.getNote());
    }

    @Test
    void updateApproval_wrongMandor_throws() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(4L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(100)
        .tujuan("Pabrik")
        .status(StatusAssignment.TIBA)
        .build();
    when(repository.findById(4L)).thenReturn(java.util.Optional.of(assignment));

    assertThrows(IllegalArgumentException.class, () ->
        service.updateApproval(4L, "other@mysawit.id", ApprovalAssignment.APPROVED, null));
    }

    @Test
    void updateApproval_nullApproval_throws() {
    assertThrows(IllegalArgumentException.class, () ->
        service.updateApproval(1L, "mandor@mysawit.id", null, null));
    }
}
