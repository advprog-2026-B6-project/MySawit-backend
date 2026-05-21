package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.AdminApprovePengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.AdminRejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PartialRejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;

@ExtendWith(MockitoExtension.class)
class AdminPengirimanControllerTest {

    @Mock
    private PengirimanService pengirimanService;

    @InjectMocks
    private AdminPengirimanController adminPengirimanController;

    private ApprovedPengirimanResponse response;
        private Pengiriman pengiriman;
        private PengirimanAssignment assignment;

    @BeforeEach
    void setUp() {
        response = new ApprovedPengirimanResponse(
                10L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                "Mandor A",
                200.0,
                "Pabrik A",
                LocalDateTime.now(),
                StatusPengiriman.DISETUJUI);
        pengiriman = Pengiriman.builder()
                .id(UUID.randomUUID())
                .mandorId(1L)
                .supirTrukId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .status(StatusPengiriman.TIBA)
                .build();
        assignment = PengirimanAssignment.builder()
                .id(10L)
                .mandorEmail("mandor@mysawit.id")
                .supirEmail("supir@mysawit.id")
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .status(StatusAssignment.TIBA)
                .approval(ApprovalAssignment.APPROVED)
                .build();
    }

    @Test
    void testGetPengirimanDisetujuiSuccess() {
        when(pengirimanService.getPengirimanDisetujui(eq("Mandor"),
                eq(LocalDate.of(2026, 5, 1)), eq(LocalDate.of(2026, 5, 3))))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = adminPengirimanController.getPengirimanDisetujui(
                "Mandor", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void testGetPengirimanDisetujuiError() {
        when(pengirimanService.getPengirimanDisetujui(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.getPengirimanDisetujui(
                "Mandor", LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1)));
    }

    @Test
    void testApprovePengirimanFinalSuccess() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.setujuiPengirimanAdmin(eq(pengirimanId), eq(1L)))
                .thenReturn(pengiriman);

        ResponseEntity<?> result = adminPengirimanController.approvePengirimanFinal(
                pengirimanId, new AdminApprovePengirimanRequest(1L));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRejectPengirimanFinalSuccess() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.tolakPengirimanAdmin(eq(pengirimanId), eq(1L), eq("Alasan")))
                .thenReturn(pengiriman);

        ResponseEntity<?> result = adminPengirimanController.rejectPengirimanFinal(
                pengirimanId, new AdminRejectPengirimanRequest(1L, "Alasan"));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRejectPengirimanFinalError() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.tolakPengirimanAdmin(eq(pengirimanId), eq(1L), eq("Alasan")))
                .thenThrow(new IllegalArgumentException("Admin tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.rejectPengirimanFinal(
                pengirimanId, new AdminRejectPengirimanRequest(1L, "Alasan")));
    }

    @Test
    void testRejectPengirimanFinalParsialSuccess() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.tolakPengirimanParsialAdmin(eq(pengirimanId), eq(1L), eq(120.0), eq("Alasan")))
                .thenReturn(pengiriman);

        ResponseEntity<?> result = adminPengirimanController.rejectPengirimanFinalParsial(
                pengirimanId, new PartialRejectPengirimanRequest(1L, 120.0, "Alasan"));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testRejectPengirimanFinalParsialError() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.tolakPengirimanParsialAdmin(eq(pengirimanId), eq(1L), eq(120.0), eq("Alasan")))
                .thenThrow(new IllegalArgumentException("Data tidak valid"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.rejectPengirimanFinalParsial(
                pengirimanId, new PartialRejectPengirimanRequest(1L, 120.0, "Alasan")));
    }

        @Test
        void testRejectAssignmentFinalParsialSuccess() {
                when(pengirimanService.tolakAssignmentFinalParsialAdmin(eq(10L), eq(1L), eq(120.0), eq("Alasan")))
                                .thenReturn(assignment);

                ResponseEntity<?> result = adminPengirimanController.rejectAssignmentFinalParsial(
                                10L, new PartialRejectPengirimanRequest(1L, 120.0, "Alasan"));

                assertEquals(HttpStatus.OK, result.getStatusCode());
        }

    @Test
    void testRejectAssignmentFinalParsialError() {
        when(pengirimanService.tolakAssignmentFinalParsialAdmin(eq(10L), eq(1L), eq(120.0), eq("Alasan")))
                .thenThrow(new IllegalArgumentException("Data tidak valid"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.rejectAssignmentFinalParsial(
                10L, new PartialRejectPengirimanRequest(1L, 120.0, "Alasan")));
    }

    @Test
    void testApprovePengirimanFinalError() {
        UUID pengirimanId = pengiriman.getId();
        when(pengirimanService.setujuiPengirimanAdmin(eq(pengirimanId), eq(1L)))
                .thenThrow(new IllegalArgumentException("Admin tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.approvePengirimanFinal(
                pengirimanId, new AdminApprovePengirimanRequest(1L)));
    }

    @Test
    void testApproveAssignmentFinalSuccess() {
        when(pengirimanService.setujuiAssignmentFinalAdmin(10L, 1L)).thenReturn(assignment);

        ResponseEntity<?> result = adminPengirimanController.approveAssignmentFinal(
                10L, new AdminApprovePengirimanRequest(1L));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void testApproveAssignmentFinalError() {
        when(pengirimanService.setujuiAssignmentFinalAdmin(10L, 1L))
                .thenThrow(new IllegalArgumentException("Admin tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.approveAssignmentFinal(
                10L, new AdminApprovePengirimanRequest(1L)));
    }

    @Test
    void testRejectAssignmentFinalSuccess() {
        when(pengirimanService.tolakAssignmentFinalAdmin(10L, 1L, "Alasan")).thenReturn(assignment);

        ResponseEntity<?> result = adminPengirimanController.rejectAssignmentFinal(
                10L, new AdminRejectPengirimanRequest(1L, "Alasan"));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void testRejectAssignmentFinalError() {
        when(pengirimanService.tolakAssignmentFinalAdmin(10L, 1L, "Alasan"))
                .thenThrow(new IllegalArgumentException("Data tidak valid"));

        assertThrows(IllegalArgumentException.class, () -> adminPengirimanController.rejectAssignmentFinal(
                10L, new AdminRejectPengirimanRequest(1L, "Alasan")));
    }
}
