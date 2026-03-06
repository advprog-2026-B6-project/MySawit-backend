package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuatPengirimanRequestTest {

    @Test
    void testDefaultConstructor() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();

        assertNull(request.getMandorId());
        assertNull(request.getSupirTrukId());
        assertEquals(0.0, request.getMuatanKg());
        assertNull(request.getTujuan());
    }

    @Test
    void testParameterizedConstructor() {
        UUID mandorId = UUID.randomUUID();
        UUID supirTrukId = UUID.randomUUID();

        BuatPengirimanRequest request = new BuatPengirimanRequest(
                mandorId, supirTrukId, 300.0, "Pabrik A");

        assertEquals(mandorId, request.getMandorId());
        assertEquals(supirTrukId, request.getSupirTrukId());
        assertEquals(300.0, request.getMuatanKg());
        assertEquals("Pabrik A", request.getTujuan());
    }

    @Test
    void testSetMandorId() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();
        UUID mandorId = UUID.randomUUID();

        request.setMandorId(mandorId);

        assertEquals(mandorId, request.getMandorId());
    }

    @Test
    void testSetSupirTrukId() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();
        UUID supirTrukId = UUID.randomUUID();

        request.setSupirTrukId(supirTrukId);

        assertEquals(supirTrukId, request.getSupirTrukId());
    }

    @Test
    void testSetMuatanKg() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();

        request.setMuatanKg(350.5);

        assertEquals(350.5, request.getMuatanKg());
    }

    @Test
    void testSetTujuan() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();

        request.setTujuan("Pabrik B");

        assertEquals("Pabrik B", request.getTujuan());
    }

    @Test
    void testSetAllValues() {
        BuatPengirimanRequest request = new BuatPengirimanRequest();
        UUID mandorId = UUID.randomUUID();
        UUID supirTrukId = UUID.randomUUID();

        request.setMandorId(mandorId);
        request.setSupirTrukId(supirTrukId);
        request.setMuatanKg(400.0);
        request.setTujuan("Pabrik C");

        assertEquals(mandorId, request.getMandorId());
        assertEquals(supirTrukId, request.getSupirTrukId());
        assertEquals(400.0, request.getMuatanKg());
        assertEquals("Pabrik C", request.getTujuan());
    }

    @Test
    void testWithNullMandorId() {
        UUID supirTrukId = UUID.randomUUID();
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                null, supirTrukId, 200.0, "Pabrik D");

        assertNull(request.getMandorId());
        assertEquals(supirTrukId, request.getSupirTrukId());
    }
}
