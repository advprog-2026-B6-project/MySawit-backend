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
        UUID supirTrukId = UUID.randomUUID();
        LocalDateTime waktu = LocalDateTime.now();

        response.setAssignmentId(99L);
        response.setPengirimanId(pengirimanId);
        response.setSupirTrukId(supirTrukId);
        response.setMandorId(5L);
        response.setMandorName("Mandor B");
        response.setMuatanKg(150.0);
        response.setTujuan("Pabrik B");
        response.setWaktuDisetujui(waktu);
        response.setStatus(StatusPengiriman.DITOLAK);

        assertEquals(99L, response.getAssignmentId());
        assertEquals(pengirimanId, response.getPengirimanId());
        assertEquals(supirTrukId, response.getSupirTrukId());
        assertEquals(5L, response.getMandorId());
        assertEquals("Mandor B", response.getMandorName());
        assertEquals(150.0, response.getMuatanKg());
        assertEquals("Pabrik B", response.getTujuan());
        assertEquals(waktu, response.getWaktuDisetujui());
        assertEquals(StatusPengiriman.DITOLAK, response.getStatus());
        assertNotNull(response);
    }
}
