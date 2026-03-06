package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PengirimanTest {

    @Test
    void testPengirimanCreationWithBuilder() {
        Pengiriman pengiriman = Pengiriman.builder().build();

        assertNotNull(pengiriman.getId());
        assertEquals(StatusPengiriman.MENUNGGU, pengiriman.getStatus());
        assertNotNull(pengiriman.getWaktuDibuat());
        assertNotNull(pengiriman.getWaktuDiperbarui());
    }

    @Test
    void testPengirimanCreationWithParams() {
        UUID supirId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();

        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(supirId)
                .mandorId(mandorId)
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();

        assertEquals(supirId, pengiriman.getSupirTrukId());
        assertEquals(mandorId, pengiriman.getMandorId());
        assertEquals(300.0, pengiriman.getMuatanKg());
        assertEquals("Pabrik A", pengiriman.getTujuan());
        assertEquals(StatusPengiriman.MENUNGGU, pengiriman.getStatus());
    }

    @Test
    void testMaxMuatanConstant() {
        assertEquals(400.0, Pengiriman.MAX_MUATAN_KG);
    }

    @Test
    void testIsSedangBerlangsungMemuat() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.MEMUAT);

        assertTrue(pengiriman.isSedangBerlangsung());
    }

    @Test
    void testIsSedangBerlangsungMengirim() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);

        assertTrue(pengiriman.isSedangBerlangsung());
    }

    @Test
    void testIsSedangBerlangsungMenunggu() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.MENUNGGU);

        assertFalse(pengiriman.isSedangBerlangsung());
    }

    @Test
    void testIsSedangBerlangsungTiba() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.TIBA);

        assertFalse(pengiriman.isSedangBerlangsung());
    }
}
