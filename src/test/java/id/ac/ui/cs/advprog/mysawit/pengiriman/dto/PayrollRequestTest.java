package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PayrollRequestTest {

    @Test
    void testBuilderAndGetters() {
        UUID pengirimanId = UUID.randomUUID();
        UUID supirTrukId = UUID.randomUUID();
        LocalDateTime waktu = LocalDateTime.now();

        PayrollRequest request = PayrollRequest.builder()
                .pengirimanId(pengirimanId)
                .supirTrukId(supirTrukId)
                .mandorId(1L)
                .muatanKg(100.0)
                .tujuan("Pabrik A")
                .waktuDisetujui(waktu)
                .build();

        assertEquals(pengirimanId, request.getPengirimanId());
        assertEquals(supirTrukId, request.getSupirTrukId());
        assertEquals(1L, request.getMandorId());
        assertEquals(100.0, request.getMuatanKg());
        assertEquals("Pabrik A", request.getTujuan());
        assertEquals(waktu, request.getWaktuDisetujui());
    }

    @Test
    void testNoArgsConstructor() {
        PayrollRequest request = new PayrollRequest();

        assertNotNull(request);
    }
}