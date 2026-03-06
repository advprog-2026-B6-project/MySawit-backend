package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.SupirTrukService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupirTrukControllerTest {

    @Mock
    private SupirTrukService supirTrukService;

    @InjectMocks
    private SupirTrukController supirTrukController;

    private SupirTruk supirTruk;
    private UUID supirTrukId;

    @BeforeEach
    void setUp() {
        supirTrukId = UUID.randomUUID();
        supirTruk = SupirTruk.builder()
                .id(supirTrukId)
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
    }

    @Test
    void testGetDaftarSupirBertugas() {
        SupirTruk supir1 = SupirTruk.builder()
                .nama("John")
                .nomorTelepon("08123")
                .platNomorTruk("B 1234")
                .sedangBertugas(true)
                .build();
        SupirTruk supir2 = SupirTruk.builder()
                .nama("Jane")
                .nomorTelepon("08124")
                .platNomorTruk("B 5678")
                .sedangBertugas(true)
                .build();

        List<SupirTruk> supirList = Arrays.asList(supir1, supir2);

        when(supirTrukService.getDaftarSupirBertugas()).thenReturn(supirList);

        ResponseEntity<?> response = supirTrukController.getDaftarSupirBertugas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllSupirTruk() {
        List<SupirTruk> supirList = Arrays.asList(supirTruk);

        when(supirTrukService.getAllSupirTruk()).thenReturn(supirList);

        ResponseEntity<?> response = supirTrukController.getAllSupirTruk();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetSupirTrukByIdSuccess() {
        when(supirTrukService.getSupirTrukById(supirTrukId)).thenReturn(supirTruk);

        ResponseEntity<?> response = supirTrukController.getSupirTrukById(supirTrukId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetSupirTrukByIdNotFound() {
        when(supirTrukService.getSupirTrukById(supirTrukId))
                .thenThrow(new IllegalArgumentException("Supir truk tidak ditemukan"));

        ResponseEntity<?> response = supirTrukController.getSupirTrukById(supirTrukId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testTambahSupirTruk() {
        when(supirTrukService.tambahSupirTruk(any(SupirTruk.class))).thenReturn(supirTruk);

        ResponseEntity<?> response = supirTrukController.tambahSupirTruk(supirTruk);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
