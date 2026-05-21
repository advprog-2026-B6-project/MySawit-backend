package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PengirimanAssignmentResponseTest {

    @Test
    void constructorAndGetters() {
        LocalDateTime createdAt = LocalDateTime.now();
        PengirimanAssignmentResponse response = new PengirimanAssignmentResponse(
                1L, "m", "s", 100.0, "tujuan",
                StatusAssignment.MENGIRIM, ApprovalAssignment.APPROVED, "note", createdAt);

        assertEquals(1L, response.getId());
        assertEquals("m", response.getMandorEmail());
        assertEquals("s", response.getSupirEmail());
        assertEquals(100.0, response.getMuatanKg());
        assertEquals("tujuan", response.getTujuan());
        assertEquals(StatusAssignment.MENGIRIM, response.getStatus());
        assertEquals(ApprovalAssignment.APPROVED, response.getApproval());
        assertEquals("note", response.getNote());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void setters() {
        LocalDateTime createdAt = LocalDateTime.now();
        PengirimanAssignmentResponse response = new PengirimanAssignmentResponse();
        response.setId(2L);
        response.setMandorEmail("m2");
        response.setSupirEmail("s2");
        response.setMuatanKg(55.0);
        response.setTujuan("pabrik");
        response.setStatus(StatusAssignment.TIBA);
        response.setApproval(ApprovalAssignment.REJECTED);
        response.setNote("reject");
        response.setCreatedAt(createdAt);

        assertEquals(2L, response.getId());
        assertEquals("m2", response.getMandorEmail());
        assertEquals("s2", response.getSupirEmail());
        assertEquals(55.0, response.getMuatanKg());
        assertEquals("pabrik", response.getTujuan());
        assertEquals(StatusAssignment.TIBA, response.getStatus());
        assertEquals(ApprovalAssignment.REJECTED, response.getApproval());
        assertEquals("reject", response.getNote());
        assertEquals(createdAt, response.getCreatedAt());
    }
}
