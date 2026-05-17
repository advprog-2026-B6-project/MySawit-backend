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

@ExtendWith(MockitoExtension.class)
class PengirimanAssignmentServiceImplTest {

    @Mock
    private PengirimanAssignmentRepository repository;

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
}
