package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PartialRejectPengirimanRequestTest {

    @Test
    void testPartialRejectPengirimanRequestConstructorAndGetter() {
        PartialRejectPengirimanRequest request = new PartialRejectPengirimanRequest(10L, 120.0, "Alasan");

        assertEquals(10L, request.getAdminId());
        assertEquals(120.0, request.getMuatanKgDiakui());
        assertEquals("Alasan", request.getAlasanPenolakan());
    }

    @Test
    void testPartialRejectPengirimanRequestSetter() {
        PartialRejectPengirimanRequest request = new PartialRejectPengirimanRequest();
        request.setAdminId(5L);
        request.setMuatanKgDiakui(80.0);
        request.setAlasanPenolakan("Lainnya");

        assertEquals(5L, request.getAdminId());
        assertEquals(80.0, request.getMuatanKgDiakui());
        assertEquals("Lainnya", request.getAlasanPenolakan());
    }
}
