package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.BuatPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UbahStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PengirimanControllerTest {

    @Mock
    private PengirimanService pengirimanService;

    @InjectMocks
    private PengirimanController pengirimanController;

    private UUID mandorId;
    private UUID supirTrukId;
    private Pengiriman pengiriman;

    @BeforeEach
    void setUp() {
        mandorId = UUID.randomUUID();
        supirTrukId = UUID.randomUUID();
        pengiriman = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandorId)
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();
    }

    @Test
    void testBuatPengirimanSuccess() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                mandorId, supirTrukId, 300.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(any(), any(), eq(300.0), eq("Pabrik A")))
                .thenReturn(pengiriman);

        ResponseEntity<?> response = pengirimanController.buatPengiriman(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testBuatPengirimanMuatanMelebihiBatas() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                mandorId, supirTrukId, 500.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(any(), any(), eq(500.0), eq("Pabrik A")))
                .thenThrow(new IllegalArgumentException("Muatan melebihi batas maksimal"));

        ResponseEntity<?> response = pengirimanController.buatPengiriman(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUbahStatusPengirimanSuccess() {
        UUID pengirimanId = pengiriman.getId();
        pengiriman.setStatus(StatusPengiriman.MEMUAT);

        UbahStatusRequest request = new UbahStatusRequest(supirTrukId, StatusPengiriman.MEMUAT);

        when(pengirimanService.ubahStatusPengiriman(eq(pengirimanId), any(), any()))
                .thenReturn(pengiriman);

        ResponseEntity<?> response = pengirimanController.ubahStatusPengiriman(pengirimanId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDaftarPengirimanSupir() {
        Pengiriman pengiriman2 = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandorId)
                .muatanKg(200.0)
                .tujuan("Pabrik B")
                .build();
        List<Pengiriman> pengirimanList = Arrays.asList(pengiriman, pengiriman2);

        when(pengirimanService.getDaftarPengirimanSupir(supirTrukId))
                .thenReturn(pengirimanList);

        ResponseEntity<?> response = pengirimanController.getDaftarPengirimanSupir(supirTrukId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDaftarPengirimanBerlangsung() {
        pengiriman.setStatus(StatusPengiriman.MEMUAT);
        List<Pengiriman> pengirimanList = Arrays.asList(pengiriman);

        when(pengirimanService.getDaftarPengirimanBerlangsung())
                .thenReturn(pengirimanList);

        ResponseEntity<?> response = pengirimanController.getDaftarPengirimanBerlangsung();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetPengirimanById() {
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanService.getPengirimanById(pengirimanId))
                .thenReturn(pengiriman);

        ResponseEntity<?> response = pengirimanController.getPengirimanById(pengirimanId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAllPengiriman() {
        List<Pengiriman> pengirimanList = Arrays.asList(pengiriman);

        when(pengirimanService.getAllPengiriman())
                .thenReturn(pengirimanList);

        ResponseEntity<?> response = pengirimanController.getAllPengiriman();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
