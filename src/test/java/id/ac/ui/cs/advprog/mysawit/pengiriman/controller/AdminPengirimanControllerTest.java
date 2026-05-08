package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPengirimanControllerTest {

    @Mock
    private PengirimanService pengirimanService;

    @InjectMocks
    private AdminPengirimanController adminPengirimanController;

    private ApprovedPengirimanResponse response;

    @BeforeEach
    void setUp() {
        response = new ApprovedPengirimanResponse(
                UUID.randomUUID(), UUID.randomUUID(), 1L, "Mandor A", 200.0, "Pabrik A",
                LocalDateTime.now(), StatusPengiriman.DISETUJUI);
    }

    @Test
    void testGetPengirimanDisetujuiSuccess() {
        when(pengirimanService.getPengirimanDisetujui(eq("Mandor"),
                eq(LocalDate.of(2026, 5, 1)), eq(LocalDate.of(2026, 5, 3))))
                .thenReturn(List.of(response));

        ResponseEntity<?> result = adminPengirimanController.getPengirimanDisetujui(
                "Mandor", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void testGetPengirimanDisetujuiError() {
        when(pengirimanService.getPengirimanDisetujui(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai"));

        ResponseEntity<?> result = adminPengirimanController.getPengirimanDisetujui(
                "Mandor", LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
