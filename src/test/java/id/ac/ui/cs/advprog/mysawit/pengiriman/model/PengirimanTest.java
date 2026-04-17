package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PengirimanTest {

    @Test
    void testPengirimanCreationWithBuilder() {
        Pengiriman pengiriman = Pengiriman.builder().build();

        assertNotNull(pengiriman.getId());
        assertEquals(StatusPengiriman.MENUNGGU, pengiriman.getStatus());
        assertNotNull(pengiriman.getWaktuDibuat());
        assertNotNull(pengiriman.getWaktuDiperbarui());
        assertNull(pengiriman.getWaktuDisetujui());
        assertNull(pengiriman.getWaktuDitolak());
        assertNull(pengiriman.getAlasanPenolakan());
    }

    @Test
    void testPengirimanCreationWithParams() {
        UUID supirId = UUID.randomUUID();
        Long mandorId = 1L;

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

    @Test
    void testIsSedangBerlangsungDisetujui() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.DISETUJUI);

        assertFalse(pengiriman.isSedangBerlangsung());
    }

    @Test
    void testIsSedangBerlangsungDitolak() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setStatus(StatusPengiriman.DITOLAK);

        assertFalse(pengiriman.isSedangBerlangsung());
    }

    @Test
    void testSetStatusUpdatesWaktuDiperbarui() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        LocalDateTime waktuAwal = pengiriman.getWaktuDiperbarui();

        pengiriman.setStatus(StatusPengiriman.MEMUAT);

        // The waktuDiperbarui should be updated to current time (>= waktuAwal)
        assertTrue(pengiriman.getWaktuDiperbarui().compareTo(waktuAwal) >= 0);
    }

    @Test
    void testSetStatusDisetujuiUpdatesWaktuDisetujui() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        assertNull(pengiriman.getWaktuDisetujui());

        pengiriman.setStatus(StatusPengiriman.DISETUJUI);

        assertNotNull(pengiriman.getWaktuDisetujui());
    }

    @Test
    void testSetStatusDitolakUpdatesWaktuDitolak() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        assertNull(pengiriman.getWaktuDitolak());

        pengiriman.setStatus(StatusPengiriman.DITOLAK);

        assertNotNull(pengiriman.getWaktuDitolak());
    }

    @Test
    void testSetId() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        UUID newId = UUID.randomUUID();
        pengiriman.setId(newId);

        assertEquals(newId, pengiriman.getId());
    }

    @Test
    void testSetSupirTrukId() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        UUID supirId = UUID.randomUUID();
        pengiriman.setSupirTrukId(supirId);

        assertEquals(supirId, pengiriman.getSupirTrukId());
    }

    @Test
    void testSetMandorId() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        Long mandorId = 42L;
        pengiriman.setMandorId(mandorId);

        assertEquals(mandorId, pengiriman.getMandorId());
    }

    @Test
    void testSetMuatanKg() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setMuatanKg(350.0);

        assertEquals(350.0, pengiriman.getMuatanKg());
    }

    @Test
    void testSetTujuan() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        pengiriman.setTujuan("Pabrik Baru");

        assertEquals("Pabrik Baru", pengiriman.getTujuan());
    }

    @Test
    void testSetWaktuDibuat() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        LocalDateTime waktuBaru = LocalDateTime.now().plusDays(1);
        pengiriman.setWaktuDibuat(waktuBaru);

        assertEquals(waktuBaru, pengiriman.getWaktuDibuat());
    }

    @Test
    void testSetWaktuDiperbarui() {
        Pengiriman pengiriman = Pengiriman.builder().build();
        LocalDateTime waktuBaru = LocalDateTime.now().plusDays(1);
        pengiriman.setWaktuDiperbarui(waktuBaru);

        assertEquals(waktuBaru, pengiriman.getWaktuDiperbarui());
    }

    @Test
    void testNoArgsConstructor() {
        Pengiriman pengiriman = new Pengiriman();

        // NoArgsConstructor with @Builder.Default still initializes default values
        assertNotNull(pengiriman.getId());
        assertNull(pengiriman.getSupirTrukId());
        assertNull(pengiriman.getMandorId());
        assertEquals(0.0, pengiriman.getMuatanKg());
        assertNull(pengiriman.getTujuan());
        assertEquals(StatusPengiriman.MENUNGGU, pengiriman.getStatus());
        assertNotNull(pengiriman.getWaktuDibuat());
        assertNotNull(pengiriman.getWaktuDiperbarui());
        assertNull(pengiriman.getWaktuDisetujui());
        assertNull(pengiriman.getWaktuDitolak());
        assertNull(pengiriman.getAlasanPenolakan());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID supirId = UUID.randomUUID();
        Long mandorId = 7L;
        LocalDateTime waktuDibuat = LocalDateTime.now();
        LocalDateTime waktuDiperbarui = LocalDateTime.now();
    LocalDateTime waktuDisetujui = LocalDateTime.now();
    LocalDateTime waktuDitolak = LocalDateTime.now();

        Pengiriman pengiriman = new Pengiriman(
                id, supirId, mandorId, 200.0, "Tujuan Test",
        StatusPengiriman.MEMUAT, waktuDibuat, waktuDiperbarui, waktuDisetujui,
        waktuDitolak, "Alasan"
        );

        assertEquals(id, pengiriman.getId());
        assertEquals(supirId, pengiriman.getSupirTrukId());
        assertEquals(mandorId, pengiriman.getMandorId());
        assertEquals(200.0, pengiriman.getMuatanKg());
        assertEquals("Tujuan Test", pengiriman.getTujuan());
        assertEquals(StatusPengiriman.MEMUAT, pengiriman.getStatus());
        assertEquals(waktuDibuat, pengiriman.getWaktuDibuat());
        assertEquals(waktuDiperbarui, pengiriman.getWaktuDiperbarui());
        assertEquals(waktuDisetujui, pengiriman.getWaktuDisetujui());
        assertEquals(waktuDitolak, pengiriman.getWaktuDitolak());
        assertEquals("Alasan", pengiriman.getAlasanPenolakan());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID supirId = UUID.randomUUID();
        Long mandorId = 99L;
        LocalDateTime waktu = LocalDateTime.now();
    LocalDateTime waktuDisetujui = LocalDateTime.now();
    LocalDateTime waktuDitolak = LocalDateTime.now();

        Pengiriman pengiriman1 = new Pengiriman(
                id, supirId, mandorId, 200.0, "Tujuan",
        StatusPengiriman.MENUNGGU, waktu, waktu, waktuDisetujui,
        waktuDitolak, "Catatan"
        );
        Pengiriman pengiriman2 = new Pengiriman(
                id, supirId, mandorId, 200.0, "Tujuan",
        StatusPengiriman.MENUNGGU, waktu, waktu, waktuDisetujui,
        waktuDitolak, "Catatan"
        );

        assertEquals(pengiriman1, pengiriman2);
        assertEquals(pengiriman1.hashCode(), pengiriman2.hashCode());
    }

    @Test
    void testNotEquals() {
        Pengiriman pengiriman1 = Pengiriman.builder()
                .muatanKg(100.0)
                .tujuan("A")
                .build();
        Pengiriman pengiriman2 = Pengiriman.builder()
                .muatanKg(200.0)
                .tujuan("B")
                .build();

        assertNotEquals(pengiriman1, pengiriman2);
    }

    @Test
    void testToString() {
        Pengiriman pengiriman = Pengiriman.builder()
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();

        String toString = pengiriman.toString();

        assertTrue(toString.contains("300.0"));
        assertTrue(toString.contains("Pabrik A"));
    }

    @Test
    void testPengirimanWithCustomId() {
        UUID customId = UUID.randomUUID();
        Pengiriman pengiriman = Pengiriman.builder()
                .id(customId)
                .muatanKg(100.0)
                .tujuan("Test")
                .build();

        assertEquals(customId, pengiriman.getId());
    }

    @Test
    void testBuilderWithCustomStatus() {
        Pengiriman pengiriman = Pengiriman.builder()
                .status(StatusPengiriman.MEMUAT)
                .build();

        assertEquals(StatusPengiriman.MEMUAT, pengiriman.getStatus());
    }

    @Test
    void testBuilderWithCustomWaktu() {
        LocalDateTime customWaktuDibuat = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime customWaktuDiperbarui = LocalDateTime.of(2024, 1, 2, 15, 0);

        Pengiriman pengiriman = Pengiriman.builder()
                .waktuDibuat(customWaktuDibuat)
                .waktuDiperbarui(customWaktuDiperbarui)
                .build();

        assertEquals(customWaktuDibuat, pengiriman.getWaktuDibuat());
        assertEquals(customWaktuDiperbarui, pengiriman.getWaktuDiperbarui());
    }
}
