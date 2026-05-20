package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

class ApprovedPengirimanResponseTest {

    @Test
    void testApprovedPengirimanResponseConstructor() {
        UUID pengirimanId = UUID.randomUUID();
        UUID supirTrukId = UUID.randomUUID();
        LocalDateTime waktu = LocalDateTime.now();

        ApprovedPengirimanResponse response = new ApprovedPengirimanResponse(
        10L,
        pengirimanId,
        supirTrukId,
        1L,
        "Mandor A",
        200.0,
        "Pabrik A",
        waktu,
        StatusPengiriman.DISETUJUI);

    assertEquals(10L, response.getAssignmentId());
        assertEquals(pengirimanId, response.getPengirimanId());
        assertEquals(supirTrukId, response.getSupirTrukId());
        assertEquals(1L, response.getMandorId());
        assertEquals("Mandor A", response.getMandorName());
        assertEquals(200.0, response.getMuatanKg());
        assertEquals("Pabrik A", response.getTujuan());
        assertEquals(waktu, response.getWaktuDisetujui());
        assertEquals(StatusPengiriman.DISETUJUI, response.getStatus());
    }

    @Test
    void testApprovedPengirimanResponseSetters() {
        ApprovedPengirimanResponse response = new ApprovedPengirimanResponse();
        UUID pengirimanId = UUID.randomUUID();

        response.setPengirimanId(pengirimanId);
        response.setMandorName("Mandor B");

        assertEquals(pengirimanId, response.getPengirimanId());
        assertEquals("Mandor B", response.getMandorName());
        assertNotNull(response);
    }
}
