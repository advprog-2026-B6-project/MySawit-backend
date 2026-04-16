package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.model.Role;
import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PengirimanServiceImplTest {

    @Mock
    private PengirimanRepository pengirimanRepository;

    @Mock
    private SupirTrukRepository supirTrukRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PengirimanServiceImpl pengirimanService;

    private Long mandorId;
    private UUID supirTrukId;
    private SupirTruk supirTruk;
    private User mandorUser;

    @BeforeEach
    void setUp() {
        mandorId = 1L;
        supirTrukId = UUID.randomUUID();
        supirTruk = SupirTruk.builder()
                .id(supirTrukId)
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        mandorUser = new User("Ahmad Mandor", "ahmad", "secret", Role.MANDOR, "CERT-001");
    }

    @Test
    void testBuatPengirimanSuccess() {
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);
        when(supirTrukRepository.save(any(SupirTruk.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.buatPengiriman(mandorId, supirTrukId, 300.0, "Pabrik A");

        assertNotNull(result);
        assertEquals(supirTrukId, result.getSupirTrukId());
        assertEquals(mandorId, result.getMandorId());
        assertEquals(300.0, result.getMuatanKg());
        assertEquals("Pabrik A", result.getTujuan());
        assertEquals(StatusPengiriman.MENUNGGU, result.getStatus());
        assertTrue(supirTruk.isSedangBertugas());
    }

    @Test
    void testBuatPengirimanMuatanMelebihiBatas() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, 500.0, "Pabrik A"));

        assertTrue(exception.getMessage().contains("Muatan melebihi batas maksimal"));
    }

    @Test
    void testBuatPengirimanMuatanTepat400Kg() {
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);
        when(supirTrukRepository.save(any(SupirTruk.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.buatPengiriman(mandorId, supirTrukId, 400.0, "Pabrik A");

        assertNotNull(result);
        assertEquals(400.0, result.getMuatanKg());
    }

    @Test
    void testBuatPengirimanMuatanNolAtauNegatif() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, 0, "Pabrik A"));

        assertTrue(exception.getMessage().contains("Muatan harus lebih dari 0 kg"));

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, -100, "Pabrik A"));

        assertTrue(exception2.getMessage().contains("Muatan harus lebih dari 0 kg"));
    }

    @Test
    void testBuatPengirimanMandorTidakDitemukan() {
        when(userRepository.findById(mandorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, 300.0, "Pabrik A"));

        assertTrue(exception.getMessage().contains("Mandor tidak ditemukan"));
    }

    @Test
    void testBuatPengirimanUserBukanMandor() {
        User bukanMandor = new User("Budi Buruh", "budi", "secret", Role.BURUH, null);
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(bukanMandor));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, 300.0, "Pabrik A"));

        assertTrue(exception.getMessage().contains("bukan seorang Mandor"));
    }

    @Test
    void testBuatPengirimanSupirTidakDitemukan() {
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.buatPengiriman(mandorId, supirTrukId, 300.0, "Pabrik A"));

        assertTrue(exception.getMessage().contains("Supir truk tidak ditemukan"));
    }

    @Test
    void testUbahStatusPengirimanSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.ubahStatusPengiriman(
                pengirimanId, supirTrukId, StatusPengiriman.MEMUAT);

        assertEquals(StatusPengiriman.MEMUAT, result.getStatus());
    }

    @Test
    void testUbahStatusPengirimanTransisiLengkap() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));
        when(supirTrukRepository.save(any(SupirTruk.class))).thenAnswer(i -> i.getArguments()[0]);

        // MENUNGGU -> MEMUAT
        pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MEMUAT);
        assertEquals(StatusPengiriman.MEMUAT, pengiriman.getStatus());

        // MEMUAT -> MENGIRIM
        pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MENGIRIM);
        assertEquals(StatusPengiriman.MENGIRIM, pengiriman.getStatus());

        // MENGIRIM -> TIBA
        pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.TIBA);
        assertEquals(StatusPengiriman.TIBA, pengiriman.getStatus());
    }

    @Test
    void testUbahStatusKeTibaSupirTrukTidakDitemukan() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.TIBA));

        assertTrue(exception.getMessage().contains("Supir truk tidak ditemukan"));
    }

    @Test
    void testUbahStatusPengirimanBukanSupirYangDitugaskan() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        UUID pengirimanId = pengiriman.getId();
        UUID supirLainId = UUID.randomUUID();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirLainId, StatusPengiriman.MEMUAT));

        assertTrue(exception.getMessage().contains("Hanya supir yang ditugaskan"));
    }

    @Test
    void testUbahStatusPengirimanTransisiTidakValid() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        // Coba langsung ke TIBA dari MENUNGGU
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.TIBA));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    @Test
    void testGetDaftarPengirimanSupir() {
        Pengiriman pengiriman1 = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        Pengiriman pengiriman2 = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik B");

        when(pengirimanRepository.findBySupirTrukId(supirTrukId))
                .thenReturn(Arrays.asList(pengiriman1, pengiriman2));

        List<Pengiriman> result = pengirimanService.getDaftarPengirimanSupir(supirTrukId);

        assertEquals(2, result.size());
        verify(pengirimanRepository).findBySupirTrukId(supirTrukId);
    }

    @Test
    void testGetDaftarPengirimanBerlangsung() {
        Pengiriman pengiriman1 = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        pengiriman1.setStatus(StatusPengiriman.MEMUAT);
        Pengiriman pengiriman2 = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik B");
        pengiriman2.setStatus(StatusPengiriman.MENGIRIM);

        when(pengirimanRepository.findAllSedangBerlangsung())
                .thenReturn(Arrays.asList(pengiriman1, pengiriman2));

        List<Pengiriman> result = pengirimanService.getDaftarPengirimanBerlangsung();

        assertEquals(2, result.size());
        verify(pengirimanRepository).findAllSedangBerlangsung();
    }

    @Test
    void testUbahStatusPengirimanNotFound() {
        UUID pengirimanId = UUID.randomUUID();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MEMUAT));

        assertTrue(exception.getMessage().contains("Pengiriman tidak ditemukan"));
    }

    @Test
    void testGetPengirimanByIdSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        Pengiriman result = pengirimanService.getPengirimanById(pengirimanId);

        assertNotNull(result);
        assertEquals(pengirimanId, result.getId());
        verify(pengirimanRepository).findById(pengirimanId);
    }

    @Test
    void testGetPengirimanByIdNotFound() {
        UUID pengirimanId = UUID.randomUUID();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.getPengirimanById(pengirimanId));

        assertTrue(exception.getMessage().contains("Pengiriman tidak ditemukan"));
    }

    @Test
    void testGetAllPengiriman() {
        Pengiriman pengiriman1 = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        Pengiriman pengiriman2 = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik B");

        when(pengirimanRepository.findAll()).thenReturn(Arrays.asList(pengiriman1, pengiriman2));

        List<Pengiriman> result = pengirimanService.getAllPengiriman();

        assertEquals(2, result.size());
        verify(pengirimanRepository).findAll();
    }

    @Test
    void testTransisiTidakValidDariMemuatKeMenunggu() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MEMUAT);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MENUNGGU));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    @Test
    void testTransisiTidakValidDariMemuatKeTiba() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MEMUAT);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.TIBA));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    @Test
    void testTransisiTidakValidDariMengirimKeMenunggu() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MENUNGGU));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    @Test
    void testTransisiTidakValidDariMengirimKeMemuat() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MEMUAT));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    @Test
    void testTransisiTidakValidDariTiba() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.ubahStatusPengiriman(pengirimanId, supirTrukId, StatusPengiriman.MENUNGGU));

        assertTrue(exception.getMessage().contains("Transisi status tidak valid"));
    }

    private Pengiriman createPengiriman(UUID supirTrukId, Long mandorId, 
                                         double muatanKg, String tujuan) {
        return Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandorId)
                .muatanKg(muatanKg)
                .tujuan(tujuan)
                .build();
    }
}
