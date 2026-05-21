package id.ac.ui.cs.advprog.mysawit.pengiriman.service.workflow;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PayrollRequestSender;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanNotFoundException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanStateException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PengirimanValidationRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PengirimanWorkflowService {
    private final PengirimanRepository pengirimanRepository;
    private final SupirTrukRepository supirTrukRepository;
    private final UserRepository userRepository;
    private final PayrollRequestSender payrollRequestSender;

    public PengirimanWorkflowService(PengirimanRepository pengirimanRepository,
                                     SupirTrukRepository supirTrukRepository,
                                     UserRepository userRepository,
                                     PayrollRequestSender payrollRequestSender) {
        this.pengirimanRepository = pengirimanRepository;
        this.supirTrukRepository = supirTrukRepository;
        this.userRepository = userRepository;
        this.payrollRequestSender = payrollRequestSender;
    }

    @Transactional
    public Pengiriman buatPengiriman(Long mandorId, UUID supirTrukId, double muatanKg, String tujuan) {
        PengirimanValidationRules.validateMuatanPengiriman(muatanKg, Pengiriman.MAX_MUATAN_KG);
        User mandor = findMandorById(mandorId);
        SupirTruk supirTruk = supirTrukRepository.findById(supirTrukId)
                .orElseThrow(() -> new PengirimanNotFoundException("Supir truk tidak ditemukan"));

        supirTruk.setSedangBertugas(true);
        supirTrukRepository.save(supirTruk);

        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandor.getId())
                .muatanKg(muatanKg)
                .tujuan(tujuan)
                .build();
        return pengirimanRepository.save(pengiriman);
    }

    @Transactional
    public Pengiriman ubahStatusPengiriman(UUID pengirimanId, UUID supirTrukId, StatusPengiriman statusBaru) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        if (!pengiriman.getSupirTrukId().equals(supirTrukId)) {
            throw new PengirimanAuthorizationException("Hanya supir yang ditugaskan yang dapat mengubah status pengiriman");
        }

        PengirimanValidationRules.validateStatusTransition(pengiriman.getStatus(), statusBaru);
        pengiriman.setStatus(statusBaru);

        if (statusBaru == StatusPengiriman.TIBA) {
            SupirTruk supirTruk = supirTrukRepository.findById(supirTrukId)
                    .orElseThrow(() -> new PengirimanNotFoundException("Supir truk tidak ditemukan"));
            supirTruk.setSedangBertugas(false);
            supirTrukRepository.save(supirTruk);
        }

        return pengirimanRepository.save(pengiriman);
    }

    @Transactional
    public Pengiriman setujuiPengiriman(UUID pengirimanId, Long mandorId) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        User mandor = findMandorById(mandorId);

        if (!pengiriman.getMandorId().equals(mandor.getId())) {
            throw new PengirimanAuthorizationException("Mandor tidak berhak menyetujui pengiriman ini");
        }
        validatePengirimanTiba(pengiriman);

        pengiriman.setStatus(StatusPengiriman.DISETUJUI);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman);
        return savedPengiriman;
    }

    @Transactional
    public Pengiriman tolakPengiriman(UUID pengirimanId, Long mandorId, String alasanPenolakan) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        String normalizedReason = PengirimanValidationRules.normalizeRequiredReason(alasanPenolakan);
        User mandor = findMandorById(mandorId);

        if (!pengiriman.getMandorId().equals(mandor.getId())) {
            throw new PengirimanAuthorizationException("Mandor tidak berhak menolak pengiriman ini");
        }
        validatePengirimanTiba(pengiriman);

        pengiriman.setAlasanPenolakan(normalizedReason);
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        return pengirimanRepository.save(pengiriman);
    }

    @Transactional
    public Pengiriman setujuiPengirimanAdmin(UUID pengirimanId, Long adminId) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        validateAdmin(adminId);
        validatePengirimanTiba(pengiriman);

        pengiriman.setStatus(StatusPengiriman.DISETUJUI);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman);
        return savedPengiriman;
    }

    @Transactional
    public Pengiriman tolakPengirimanAdmin(UUID pengirimanId, Long adminId, String alasanPenolakan) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        validateAdmin(adminId);
        validatePengirimanTiba(pengiriman);

        String normalizedReason = PengirimanValidationRules.normalizeRequiredReason(alasanPenolakan);
        pengiriman.setAlasanPenolakan(normalizedReason);
        pengiriman.setMuatanKgDiakui(null);
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        return pengirimanRepository.save(pengiriman);
    }

    @Transactional
    public Pengiriman tolakPengirimanParsialAdmin(UUID pengirimanId, Long adminId, double muatanKgDiakui,
                                                  String alasanPenolakan) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        validateAdmin(adminId);
        validatePengirimanTiba(pengiriman);
        PengirimanValidationRules.validateMuatanDiakui(muatanKgDiakui, pengiriman.getMuatanKg());

        String normalizedReason = PengirimanValidationRules.normalizeRequiredReason(alasanPenolakan);
        pengiriman.setAlasanPenolakan(normalizedReason);
        pengiriman.setMuatanKgDiakui(muatanKgDiakui);
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman, muatanKgDiakui);
        return savedPengiriman;
    }

    public List<Pengiriman> getDaftarPengirimanSupir(UUID supirTrukId) {
        return pengirimanRepository.findBySupirTrukId(supirTrukId);
    }

    public List<Pengiriman> getRiwayatPengirimanSupir(UUID supirTrukId, LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        PengirimanValidationRules.validateDateRange(tanggalMulai, tanggalSelesai);
        return pengirimanRepository.findRiwayatSupir(supirTrukId, tanggalMulai, tanggalSelesai);
    }

    public String getAlasanPenolakan(UUID pengirimanId, UUID supirTrukId) {
        Pengiriman pengiriman = findPengirimanById(pengirimanId);
        if (!pengiriman.getSupirTrukId().equals(supirTrukId)) {
            throw new PengirimanAuthorizationException("Hanya supir yang ditugaskan yang dapat melihat alasan penolakan");
        }
        if (pengiriman.getStatus() != StatusPengiriman.DITOLAK) {
            throw new PengirimanStateException("Pengiriman tidak berstatus ditolak");
        }
        if (pengiriman.getAlasanPenolakan() == null || pengiriman.getAlasanPenolakan().isBlank()) {
            throw new PengirimanStateException("Alasan penolakan tidak tersedia");
        }
        return pengiriman.getAlasanPenolakan();
    }

    public List<Pengiriman> getDaftarPengirimanBerlangsung() {
        return pengirimanRepository.findAllSedangBerlangsung();
    }

    public Pengiriman getPengirimanById(UUID id) {
        return findPengirimanById(id);
    }

    public List<Pengiriman> getAllPengiriman() {
        return pengirimanRepository.findAll();
    }

    private Pengiriman findPengirimanById(UUID pengirimanId) {
        return pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new PengirimanNotFoundException("Pengiriman tidak ditemukan"));
    }

    private User findMandorById(Long mandorId) {
        if (mandorId == null) {
            throw new PengirimanNotFoundException("Mandor tidak ditemukan");
        }
        User mandor = userRepository.findById(mandorId)
                .orElseThrow(() -> new PengirimanNotFoundException("Mandor tidak ditemukan"));
        if (mandor.getRole() != Role.MANDOR) {
            throw new PengirimanAuthorizationException("User dengan id " + mandorId + " bukan seorang Mandor");
        }
        return mandor;
    }

    private void validateAdmin(Long adminId) {
        if (adminId == null) {
            throw new PengirimanNotFoundException("Admin tidak ditemukan");
        }
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new PengirimanNotFoundException("Admin tidak ditemukan"));
        if (admin.getRole() != Role.ADMIN) {
            throw new PengirimanAuthorizationException("User dengan id " + adminId + " bukan seorang Admin");
        }
    }

    private void validatePengirimanTiba(Pengiriman pengiriman) {
        if (pengiriman.getStatus() != StatusPengiriman.TIBA) {
            throw new PengirimanStateException("Pengiriman belum sampai tujuan");
        }
    }
}
