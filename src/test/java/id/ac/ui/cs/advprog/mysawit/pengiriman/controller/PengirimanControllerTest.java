package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovePengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.BuatPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.RejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UbahStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S6068")
class PengirimanControllerTest {

    @Mock
    private PengirimanService pengirimanService;

        @Mock
        private UserRepository userRepository;

    @InjectMocks
    private PengirimanController pengirimanController;

    private Long mandorId;
    private UUID supirTrukId;
    private Pengiriman pengiriman;

    @BeforeEach
    void setUp() {
        mandorId = 1L;
        supirTrukId = UUID.randomUUID();
        pengiriman = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandorId)
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();
    }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

    @Test
    void testBuatPengirimanSuccess() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                mandorId, supirTrukId, 300.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(eq(mandorId), eq(supirTrukId), eq(300.0), eq("Pabrik A")))
                .thenReturn(pengiriman);

        ResponseEntity<?> response = pengirimanController.buatPengiriman(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testBuatPengirimanMuatanMelebihiBatas() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                mandorId, supirTrukId, 500.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(eq(mandorId), eq(supirTrukId), eq(500.0), eq("Pabrik A")))
                .thenThrow(new IllegalArgumentException("Muatan melebihi batas maksimal"));

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
    }

    @Test
    void testBuatPengirimanMandorTidakDitemukan() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                99L, supirTrukId, 300.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(eq(99L), eq(supirTrukId), eq(300.0), eq("Pabrik A")))
                .thenThrow(new IllegalArgumentException("Mandor tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
    }

    @Test
    void testBuatPengirimanUserBukanMandor() {
        Long bukanMandorId = 2L;
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                bukanMandorId, supirTrukId, 300.0, "Pabrik A");

        when(pengirimanService.buatPengiriman(eq(bukanMandorId), eq(supirTrukId), eq(300.0), eq("Pabrik A")))
                .thenThrow(new IllegalArgumentException("bukan seorang Mandor"));

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
    }

    @Test
    void testBuatPengirimanWithNullMandorId() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                null, supirTrukId, 300.0, "Pabrik A");

        User mandor = new User(mandorId, "Mandor", "mandor", "pw", Role.MANDOR, null, null);
        when(userRepository.findByUsername("mandor")).thenReturn(Optional.of(mandor));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("mandor", null, List.of()));

        when(pengirimanService.buatPengiriman(eq(mandorId), eq(supirTrukId), eq(300.0), eq("Pabrik A")))
                .thenReturn(pengiriman);

        ResponseEntity<?> response = pengirimanController.buatPengiriman(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testBuatPengirimanWithNullMandorIdWithoutAuth() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                null, supirTrukId, 300.0, "Pabrik A");

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
    }

    @Test
    void testBuatPengirimanWithNullMandorIdAuthenticationNotAuthenticated() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                null, supirTrukId, 300.0, "Pabrik A");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
    }

    @Test
    void testBuatPengirimanWithNullMandorIdUserNotFound() {
        BuatPengirimanRequest request = new BuatPengirimanRequest(
                null, supirTrukId, 300.0, "Pabrik A");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing", null, List.of()));
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.buatPengiriman(request));
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
    void testUbahStatusPengirimanError() {
        UUID pengirimanId = pengiriman.getId();

        UbahStatusRequest request = new UbahStatusRequest(supirTrukId, StatusPengiriman.MEMUAT);

        when(pengirimanService.ubahStatusPengiriman(eq(pengirimanId), any(), any()))
                .thenThrow(new IllegalArgumentException("Transisi status tidak valid"));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanController.ubahStatusPengiriman(pengirimanId, request));
    }

        @Test
        void testApprovePengirimanSuccess() {
                UUID pengirimanId = pengiriman.getId();
                ApprovePengirimanRequest request = new ApprovePengirimanRequest(mandorId);

                when(pengirimanService.setujuiPengiriman(eq(pengirimanId), eq(mandorId)))
                                .thenReturn(pengiriman);

                ResponseEntity<?> response = pengirimanController.approvePengiriman(pengirimanId, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
        }

        @Test
        void testApprovePengirimanError() {
                UUID pengirimanId = pengiriman.getId();
                ApprovePengirimanRequest request = new ApprovePengirimanRequest(mandorId);

                when(pengirimanService.setujuiPengiriman(eq(pengirimanId), eq(mandorId)))
                                .thenThrow(new IllegalArgumentException("Pengiriman belum sampai tujuan"));

                assertThrows(IllegalArgumentException.class,
                                () -> pengirimanController.approvePengiriman(pengirimanId, request));
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
    void testGetDaftarPengirimanSupirError() {
        when(pengirimanService.getDaftarPengirimanSupir(supirTrukId))
                .thenThrow(new IllegalArgumentException("Supir tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.getDaftarPengirimanSupir(supirTrukId));
    }

        @Test
        void testRejectPengirimanSuccess() {
                UUID pengirimanId = pengiriman.getId();
                RejectPengirimanRequest request = new RejectPengirimanRequest(mandorId, "Tidak sesuai");

                when(pengirimanService.tolakPengiriman(eq(pengirimanId), eq(mandorId), eq("Tidak sesuai")))
                                .thenReturn(pengiriman);

                ResponseEntity<?> response = pengirimanController.rejectPengiriman(pengirimanId, request);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
        }

        @Test
        void testRejectPengirimanError() {
                UUID pengirimanId = pengiriman.getId();
                RejectPengirimanRequest request = new RejectPengirimanRequest(mandorId, "");

                when(pengirimanService.tolakPengiriman(eq(pengirimanId), eq(mandorId), eq("")))
                                .thenThrow(new IllegalArgumentException("Alasan penolakan wajib diisi"));

                assertThrows(IllegalArgumentException.class,
                                () -> pengirimanController.rejectPengiriman(pengirimanId, request));
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
    void testGetPengirimanByIdError() {
        UUID pengirimanId = UUID.randomUUID();

        when(pengirimanService.getPengirimanById(pengirimanId))
                .thenThrow(new IllegalArgumentException("Pengiriman tidak ditemukan"));

        assertThrows(IllegalArgumentException.class, () -> pengirimanController.getPengirimanById(pengirimanId));
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
