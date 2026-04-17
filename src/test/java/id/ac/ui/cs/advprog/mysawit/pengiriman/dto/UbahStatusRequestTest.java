package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UbahStatusRequestTest {

    @Test
    void testDefaultConstructor() {
        UbahStatusRequest request = new UbahStatusRequest();

        assertNull(request.getSupirTrukId());
        assertNull(request.getStatusBaru());
    }

    @Test
    void testParameterizedConstructor() {
        UUID supirTrukId = UUID.randomUUID();

        UbahStatusRequest request = new UbahStatusRequest(supirTrukId, StatusPengiriman.MEMUAT);

        assertEquals(supirTrukId, request.getSupirTrukId());
        assertEquals(StatusPengiriman.MEMUAT, request.getStatusBaru());
    }

    @Test
    void testSetSupirTrukId() {
        UbahStatusRequest request = new UbahStatusRequest();
        UUID supirTrukId = UUID.randomUUID();

        request.setSupirTrukId(supirTrukId);

        assertEquals(supirTrukId, request.getSupirTrukId());
    }

    @Test
    void testSetStatusBaru() {
        UbahStatusRequest request = new UbahStatusRequest();

        request.setStatusBaru(StatusPengiriman.MENGIRIM);

        assertEquals(StatusPengiriman.MENGIRIM, request.getStatusBaru());
    }

    @Test
    void testSetAllValues() {
        UbahStatusRequest request = new UbahStatusRequest();
        UUID supirTrukId = UUID.randomUUID();

        request.setSupirTrukId(supirTrukId);
        request.setStatusBaru(StatusPengiriman.TIBA);

        assertEquals(supirTrukId, request.getSupirTrukId());
        assertEquals(StatusPengiriman.TIBA, request.getStatusBaru());
    }

    @Test
    void testAllStatusValues() {
        UUID supirTrukId = UUID.randomUUID();

        UbahStatusRequest requestMemuat = new UbahStatusRequest(supirTrukId, StatusPengiriman.MEMUAT);
        UbahStatusRequest requestMengirim = new UbahStatusRequest(supirTrukId, StatusPengiriman.MENGIRIM);
        UbahStatusRequest requestTiba = new UbahStatusRequest(supirTrukId, StatusPengiriman.TIBA);
        UbahStatusRequest requestMenunggu = new UbahStatusRequest(supirTrukId, StatusPengiriman.MENUNGGU);

        assertEquals(StatusPengiriman.MEMUAT, requestMemuat.getStatusBaru());
        assertEquals(StatusPengiriman.MENGIRIM, requestMengirim.getStatusBaru());
        assertEquals(StatusPengiriman.TIBA, requestTiba.getStatusBaru());
        assertEquals(StatusPengiriman.MENUNGGU, requestMenunggu.getStatusBaru());
    }
}
