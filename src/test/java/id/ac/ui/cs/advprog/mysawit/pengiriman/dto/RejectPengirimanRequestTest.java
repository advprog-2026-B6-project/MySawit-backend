package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RejectPengirimanRequestTest {

    @Test
    void testRejectPengirimanRequestConstructorAndGetter() {
        RejectPengirimanRequest request = new RejectPengirimanRequest(5L, "Alasan");

        assertEquals(5L, request.getMandorId());
        assertEquals("Alasan", request.getAlasanPenolakan());
    }

    @Test
    void testRejectPengirimanRequestSetter() {
        RejectPengirimanRequest request = new RejectPengirimanRequest();
        request.setMandorId(7L);
        request.setAlasanPenolakan("Tidak sesuai");

        assertEquals(7L, request.getMandorId());
        assertEquals("Tidak sesuai", request.getAlasanPenolakan());
    }
}