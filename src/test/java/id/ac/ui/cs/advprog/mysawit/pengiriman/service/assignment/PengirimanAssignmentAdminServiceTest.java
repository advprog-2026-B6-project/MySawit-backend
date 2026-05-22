package id.ac.ui.cs.advprog.mysawit.pengiriman.service.assignment;

import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PayrollRequestSender;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PayrollRequestFactory;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PengirimanAssignmentAdminServiceTest {
    @Mock
    private PengirimanAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PayrollRequestSender payrollRequestSender;

    @Mock
    private PayrollRequestFactory fullPayrollRequestFactory;

    @Mock
    private PayrollRequestFactory partialPayrollRequestFactory;

    private PengirimanAssignmentAdminService service;

    @BeforeEach
    void setUp() {
        service = new PengirimanAssignmentAdminService(
                assignmentRepository,
                userRepository,
                payrollRequestSender,
                new SupirIdentityMapper(),
                fullPayrollRequestFactory,
                partialPayrollRequestFactory
        );
    }

    @Test
    void getPengirimanDisetujui_usesRepositoryResultWhenPresent() {
        PengirimanAssignment assignment = approvedAssignment(1L, "mandor@mysawit.id", LocalDateTime.now());
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of(assignment));
        when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.empty());

        var result = service.getPengirimanDisetujui("mandor", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAssignmentId());
        assertEquals("mandor@mysawit.id", result.get(0).getMandorName());
    }

    @Test
    void getPengirimanDisetujui_filtersFallbackAssignmentWithoutCreatedAt() {
        PengirimanAssignment assignment = approvedAssignment(2L, "mandor@mysawit.id", null);
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

        var result = service.getPengirimanDisetujui(null, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        assertEquals(0, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackIncludesApprovedAssignmentWithoutFilters() {
        PengirimanAssignment assignment = approvedAssignment(3L, null, LocalDateTime.of(2026, 5, 2, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

        var result = service.getPengirimanDisetujui(null, null, null);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getAssignmentId());
    }

    @Test
    void getPengirimanDisetujui_fallbackFiltersRejectedAssignment() {
        PengirimanAssignment assignment = approvedAssignment(4L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 5, 2, 10, 0));
        assignment.setApproval(ApprovalAssignment.REJECTED);
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

        var result = service.getPengirimanDisetujui(null, null, null);

        assertEquals(0, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackSupportsOnlyStartDate() {
        PengirimanAssignment assignment = approvedAssignment(5L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 5, 2, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));
        when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.empty());

        var result = service.getPengirimanDisetujui(null, LocalDate.of(2026, 5, 1), null);

        assertEquals(1, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackSupportsOnlyEndDate() {
        PengirimanAssignment assignment = approvedAssignment(6L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 5, 2, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));
        when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.empty());

        var result = service.getPengirimanDisetujui(null, null, LocalDate.of(2026, 5, 3));

        assertEquals(1, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackMatchesMandorQuery() {
        PengirimanAssignment assignment = approvedAssignment(7L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 5, 2, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));
        when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.empty());

        var result = service.getPengirimanDisetujui("mandor", null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackFiltersBeforeStartDate() {
        PengirimanAssignment assignment = approvedAssignment(8L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 4, 30, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

        var result = service.getPengirimanDisetujui(null, LocalDate.of(2026, 5, 1), null);

        assertEquals(0, result.size());
    }

    @Test
    void getPengirimanDisetujui_fallbackFiltersAfterEndDate() {
        PengirimanAssignment assignment = approvedAssignment(9L, "mandor@mysawit.id",
                LocalDateTime.of(2026, 5, 4, 10, 0));
        when(assignmentRepository.findApprovedAssignmentsForAdmin(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

        var result = service.getPengirimanDisetujui(null, null, LocalDate.of(2026, 5, 3));

        assertEquals(0, result.size());
    }

    private PengirimanAssignment approvedAssignment(Long id, String mandorEmail, LocalDateTime createdAt) {
        return PengirimanAssignment.builder()
                .id(id)
                .mandorEmail(mandorEmail)
                .supirEmail("supir@mysawit.id")
                .muatanKg(100.0)
                .tujuan("Pabrik A")
                .approval(ApprovalAssignment.APPROVED)
                .createdAt(createdAt)
                .build();
    }
}
