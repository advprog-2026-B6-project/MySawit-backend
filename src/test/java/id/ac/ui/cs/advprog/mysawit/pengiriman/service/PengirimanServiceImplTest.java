package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;

@ExtendWith(MockitoExtension.class)
class PengirimanServiceImplTest {

    @Mock
    private PengirimanRepository pengirimanRepository;

    @Mock
    private PengirimanAssignmentRepository pengirimanAssignmentRepository;

    @Mock
    private SupirTrukRepository supirTrukRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PayrollRequestSender payrollRequestSender;

    @InjectMocks
    private PengirimanServiceImpl pengirimanService;

    private Long mandorId;
    private Long adminId;
    private UUID supirTrukId;
    private SupirTruk supirTruk;
    private User mandorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        mandorId = 1L;
    adminId = 99L;
        supirTrukId = UUID.randomUUID();
        supirTruk = SupirTruk.builder()
                .id(supirTrukId)
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        mandorUser = new User("Ahmad Mandor", "ahmad", "secret", Role.MANDOR, "CERT-001");
        adminUser = new User("Admin Utama", "admin", "secret", Role.ADMIN, null);
        mandorUser.setId(mandorId);
        adminUser.setId(adminId);
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
    void testSetujuiPengirimanSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.setujuiPengiriman(pengirimanId, mandorId);

        assertEquals(StatusPengiriman.DISETUJUI, result.getStatus());
        assertNotNull(result.getWaktuDisetujui());
        verify(payrollRequestSender).sendPayrollRequest(eq(result));
    }

    @Test
    void testSetujuiPengirimanNotFound() {
        UUID pengirimanId = UUID.randomUUID();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, mandorId));

        assertTrue(exception.getMessage().contains("Pengiriman tidak ditemukan"));
    }

    @Test
    void testSetujuiPengirimanMandorTidakDitemukan() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, mandorId));

        assertTrue(exception.getMessage().contains("Mandor tidak ditemukan"));
    }

    @Test
    void testSetujuiPengirimanMandorNull() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, null));

        assertTrue(exception.getMessage().contains("Mandor tidak ditemukan"));
    }

    @Test
    void testSetujuiPengirimanBukanMandor() {
        User bukanMandor = new User("Budi", "budi", "secret", Role.BURUH, null);
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(bukanMandor));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, mandorId));

        assertTrue(exception.getMessage().contains("bukan seorang Mandor"));
    }

    @Test
    void testSetujuiPengirimanMandorTidakSesuai() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, 99L, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, mandorId));

        assertTrue(exception.getMessage().contains("Mandor tidak berhak"));
    }

    @Test
    void testSetujuiPengirimanBelumTiba() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengiriman(pengirimanId, mandorId));

        assertTrue(exception.getMessage().contains("belum sampai tujuan"));
    }

    @Test
    void testTolakPengirimanSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.tolakPengiriman(pengirimanId, mandorId, "  Tidak sesuai  ");

        assertEquals(StatusPengiriman.DITOLAK, result.getStatus());
        assertEquals("Tidak sesuai", result.getAlasanPenolakan());
        assertNotNull(result.getWaktuDitolak());
    }

    @Test
    void testTolakPengirimanAlasanKosong() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.tolakPengiriman(pengirimanId, mandorId, "   "));

        assertTrue(exception.getMessage().contains("Alasan penolakan wajib diisi"));
    }

    @Test
    void testTolakPengirimanBelumTiba() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.MENGIRIM);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.tolakPengiriman(pengirimanId, mandorId, "Alasan"));

        assertTrue(exception.getMessage().contains("belum sampai tujuan"));
    }

    @Test
    void testTolakPengirimanMandorTidakDitemukan() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.tolakPengiriman(pengirimanId, mandorId, "Alasan"));

        assertTrue(exception.getMessage().contains("Mandor tidak ditemukan"));
    }

    @Test
    void testTolakPengirimanMandorTidakSesuai() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, 99L, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(mandorId)).thenReturn(Optional.of(mandorUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.tolakPengiriman(pengirimanId, mandorId, "Alasan"));

        assertTrue(exception.getMessage().contains("Mandor tidak berhak"));
    }

    @Test
    void testSetujuiPengirimanAdminSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.setujuiPengirimanAdmin(pengirimanId, adminId);

        assertEquals(StatusPengiriman.DISETUJUI, result.getStatus());
        verify(payrollRequestSender).sendPayrollRequest(eq(result));
    }

    @Test
    void testSetujuiPengirimanAdminBukanAdmin() {
        User bukanAdmin = new User("Budi", "budi", "secret", Role.MANDOR, null);
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(bukanAdmin));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.setujuiPengirimanAdmin(pengirimanId, adminId));

        assertTrue(exception.getMessage().contains("bukan seorang Admin"));
    }

    @Test
    void testTolakPengirimanAdminSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.tolakPengirimanAdmin(
                pengirimanId, adminId, "  Tidak sesuai  ");

        assertEquals(StatusPengiriman.DITOLAK, result.getStatus());
        assertEquals("Tidak sesuai", result.getAlasanPenolakan());
    }

    @Test
    void testTolakPengirimanParsialAdminSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(pengirimanRepository.save(any(Pengiriman.class))).thenAnswer(i -> i.getArguments()[0]);

        Pengiriman result = pengirimanService.tolakPengirimanParsialAdmin(
                pengirimanId, adminId, 120.0, "  Parsial  ");

        assertEquals(StatusPengiriman.DITOLAK, result.getStatus());
        assertEquals("Parsial", result.getAlasanPenolakan());
        assertEquals(120.0, result.getMuatanKgDiakui());
        verify(payrollRequestSender).sendPayrollRequest(eq(result), eq(120.0));
    }

    @Test
    void testTolakPengirimanParsialAdminMuatanInvalid() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.tolakPengirimanParsialAdmin(pengirimanId, adminId, 300.0, "Alasan"));

        assertTrue(exception.getMessage().contains("Kilogram diakui"));
    }

    @Test
    void testGetRiwayatPengirimanSupir() {
    Pengiriman pengiriman1 = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
    Pengiriman pengiriman2 = createPengiriman(supirTrukId, mandorId, 300.0, "Pabrik B");

    when(pengirimanRepository.findRiwayatSupir(supirTrukId,
        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3)))
        .thenReturn(Arrays.asList(pengiriman1, pengiriman2));

    List<Pengiriman> result = pengirimanService.getRiwayatPengirimanSupir(
        supirTrukId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));

    assertEquals(2, result.size());
    verify(pengirimanRepository).findRiwayatSupir(supirTrukId,
        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));
    }

    @Test
    void testGetRiwayatPengirimanSupirTanggalInvalid() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.getRiwayatPengirimanSupir(
            supirTrukId, LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1)));

    assertTrue(exception.getMessage().contains("Tanggal mulai"));
    }

    @Test
    void testGetAlasanPenolakanSuccess() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        pengiriman.setAlasanPenolakan("Tidak sesuai");
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        String alasan = pengirimanService.getAlasanPenolakan(pengirimanId, supirTrukId);

        assertEquals("Tidak sesuai", alasan);
    }

    @Test
    void testGetAlasanPenolakanBukanSupir() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        pengiriman.setAlasanPenolakan("Tidak sesuai");
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.getAlasanPenolakan(pengirimanId, UUID.randomUUID()));

        assertTrue(exception.getMessage().contains("Hanya supir"));
    }

    @Test
    void testGetAlasanPenolakanStatusBukanDitolak() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.TIBA);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.getAlasanPenolakan(pengirimanId, supirTrukId));

        assertTrue(exception.getMessage().contains("tidak berstatus"));
    }

    @Test
    void testGetAlasanPenolakanTidakTersedia() {
        Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        UUID pengirimanId = pengiriman.getId();

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.getAlasanPenolakan(pengirimanId, supirTrukId));

        assertTrue(exception.getMessage().contains("tidak tersedia"));
    }

    @Test
    void testGetPengirimanDisetujuiWithFilter() {
    PengirimanAssignment assignment1 = PengirimanAssignment.builder()
        .id(1L)
        .mandorEmail("ahmad@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(200.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .createdAt(LocalDateTime.of(2026, 5, 2, 10, 0))
        .build();

    PengirimanAssignment assignment2 = PengirimanAssignment.builder()
        .id(2L)
        .mandorEmail("budi@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(300.0)
        .tujuan("Pabrik B")
        .approval(ApprovalAssignment.APPROVED)
        .createdAt(LocalDateTime.of(2026, 5, 4, 10, 0))
        .build();

    when(pengirimanAssignmentRepository.findAll()).thenReturn(Arrays.asList(assignment1, assignment2));
    when(userRepository.findByUsername("ahmad@mysawit.id")).thenReturn(Optional.of(mandorUser));

        List<id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse> result =
                pengirimanService.getPengirimanDisetujui("Ahmad",
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));

        assertEquals(1, result.size());
        assertEquals(mandorId, result.get(0).getMandorId());
    }

    @Test
    void testGetPengirimanDisetujuiTanggalInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pengirimanService.getPengirimanDisetujui("Ahmad",
                        LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1)));

        assertTrue(exception.getMessage().contains("Tanggal mulai"));
    }

    @Test
    void testGetPengirimanDisetujuiMandorNotFound() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(3L)
        .mandorEmail("unknown@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(180.0)
        .tujuan("Pabrik C")
        .approval(ApprovalAssignment.APPROVED)
        .createdAt(LocalDateTime.of(2026, 5, 2, 9, 0))
        .build();

    when(pengirimanAssignmentRepository.findAll()).thenReturn(List.of(assignment));
    when(userRepository.findByUsername("unknown@mysawit.id")).thenReturn(Optional.empty());

    List<id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse> result =
        pengirimanService.getPengirimanDisetujui(null, null, null);

    assertEquals(1, result.size());
    assertEquals("unknown@mysawit.id", result.get(0).getMandorName());
    }

    @Test
    void testSetujuiPengirimanAdminStatusInvalid() {
    Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
    pengiriman.setStatus(StatusPengiriman.MENGIRIM);
    UUID pengirimanId = pengiriman.getId();

    when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.setujuiPengirimanAdmin(pengirimanId, adminId));

    assertTrue(exception.getMessage().contains("belum sampai"));
    }

    @Test
    void testSetujuiPengirimanAdminNullAdmin() {
    Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
    pengiriman.setStatus(StatusPengiriman.TIBA);
    UUID pengirimanId = pengiriman.getId();

    when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.setujuiPengirimanAdmin(pengirimanId, null));

    assertTrue(exception.getMessage().contains("Admin tidak ditemukan"));
    }

    @Test
    void testTolakPengirimanAdminEmptyReason() {
    Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
    pengiriman.setStatus(StatusPengiriman.TIBA);
    UUID pengirimanId = pengiriman.getId();

    when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.tolakPengirimanAdmin(pengirimanId, adminId, " "));

    assertTrue(exception.getMessage().contains("wajib diisi"));
    }

    @Test
    void testTolakPengirimanParsialAdminInvalidKgLow() {
    Pengiriman pengiriman = createPengiriman(supirTrukId, mandorId, 200.0, "Pabrik A");
    pengiriman.setStatus(StatusPengiriman.TIBA);
    UUID pengirimanId = pengiriman.getId();

    when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.tolakPengirimanParsialAdmin(pengirimanId, adminId, 0, "Rusak"));

    assertTrue(exception.getMessage().contains("lebih dari 0"));
    }

    @Test
    void testSetujuiAssignmentFinalAdminSuccess() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(5L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(5L)).thenReturn(Optional.of(assignment));
    when(pengirimanAssignmentRepository.save(any(PengirimanAssignment.class)))
        .thenAnswer(i -> i.getArguments()[0]);
    when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.of(mandorUser));

    PengirimanAssignment result = pengirimanService.setujuiAssignmentFinalAdmin(5L, adminId);

    assertEquals(ApprovalAssignment.APPROVED, result.getAdminFinalApproval());
    verify(payrollRequestSender).sendPayrollRequest(
            any(id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest.class));
    }

    @Test
    void testSetujuiAssignmentFinalAdminNotApproved() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(6L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.REJECTED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(6L)).thenReturn(Optional.of(assignment));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.setujuiAssignmentFinalAdmin(6L, adminId));

    assertTrue(exception.getMessage().contains("belum disetujui"));
    }

    @Test
    void testTolakAssignmentFinalAdminSuccess() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(7L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(7L)).thenReturn(Optional.of(assignment));
    when(pengirimanAssignmentRepository.save(any(PengirimanAssignment.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    PengirimanAssignment result = pengirimanService.tolakAssignmentFinalAdmin(7L, adminId, "Tidak sesuai");

    assertEquals(ApprovalAssignment.REJECTED, result.getAdminFinalApproval());
    assertEquals("Tidak sesuai", result.getAdminFinalNote());
    }

    @Test
    void testTolakAssignmentFinalAdminEmptyReason() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(8L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(8L)).thenReturn(Optional.of(assignment));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.tolakAssignmentFinalAdmin(8L, adminId, " "));

    assertTrue(exception.getMessage().contains("wajib diisi"));
    }

    @Test
    void testTolakAssignmentFinalParsialAdminSuccess() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(9L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(9L)).thenReturn(Optional.of(assignment));
    when(pengirimanAssignmentRepository.save(any(PengirimanAssignment.class)))
        .thenAnswer(i -> i.getArguments()[0]);
    when(userRepository.findByUsername("mandor@mysawit.id")).thenReturn(Optional.empty());

    PengirimanAssignment result = pengirimanService.tolakAssignmentFinalParsialAdmin(9L, adminId, 80.0, "Rusak");

    assertEquals(ApprovalAssignment.PARTIALLY_REJECTED, result.getAdminFinalApproval());
    assertEquals(80.0, result.getKilogramDiakui());
    verify(payrollRequestSender).sendPayrollRequest(
            any(id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest.class));
    }

    @Test
    void testTolakAssignmentFinalParsialAdminInvalidKg() {
    PengirimanAssignment assignment = PengirimanAssignment.builder()
        .id(10L)
        .mandorEmail("mandor@mysawit.id")
        .supirEmail("supir@mysawit.id")
        .muatanKg(150.0)
        .tujuan("Pabrik A")
        .approval(ApprovalAssignment.APPROVED)
        .build();

    when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    when(pengirimanAssignmentRepository.findById(10L)).thenReturn(Optional.of(assignment));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        pengirimanService.tolakAssignmentFinalParsialAdmin(10L, adminId, 150.0, "Rusak"));

    assertTrue(exception.getMessage().contains("lebih kecil"));
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
