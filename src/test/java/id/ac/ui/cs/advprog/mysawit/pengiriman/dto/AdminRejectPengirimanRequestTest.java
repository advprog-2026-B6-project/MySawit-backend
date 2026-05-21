package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AdminRejectPengirimanRequestTest {

    @Test
    void testAdminRejectPengirimanRequestConstructorAndGetter() {
        AdminRejectPengirimanRequest request = new AdminRejectPengirimanRequest(10L, "Alasan");

        assertEquals(10L, request.getAdminId());
        assertEquals("Alasan", request.getAlasanPenolakan());
    }

    @Test
    void testAdminRejectPengirimanRequestSetter() {
        AdminRejectPengirimanRequest request = new AdminRejectPengirimanRequest();
        request.setAdminId(5L);
        request.setAlasanPenolakan("Lainnya");

        assertEquals(5L, request.getAdminId());
        assertEquals("Lainnya", request.getAlasanPenolakan());
    }
}
