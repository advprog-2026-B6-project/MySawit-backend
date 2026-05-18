package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;

@Service
public class PengirimanServiceImpl implements PengirimanService {

    private final PengirimanRepository pengirimanRepository;
    private final PengirimanAssignmentRepository pengirimanAssignmentRepository;
    private final SupirTrukRepository supirTrukRepository;
    private final UserRepository userRepository;
    private final PayrollRequestSender payrollRequestSender;

    public PengirimanServiceImpl(PengirimanRepository pengirimanRepository,
                                  PengirimanAssignmentRepository pengirimanAssignmentRepository,
                                  SupirTrukRepository supirTrukRepository,
                                  UserRepository userRepository,
                                  PayrollRequestSender payrollRequestSender) {
        this.pengirimanRepository = pengirimanRepository;
        this.pengirimanAssignmentRepository = pengirimanAssignmentRepository;
        this.supirTrukRepository = supirTrukRepository;
        this.userRepository = userRepository;
        this.payrollRequestSender = payrollRequestSender;
    }

    @Override
    public Pengiriman buatPengiriman(Long mandorId, UUID supirTrukId, double muatanKg, String tujuan) {
        if (muatanKg > Pengiriman.MAX_MUATAN_KG) {
            throw new IllegalArgumentException(
                "Muatan melebihi batas maksimal. Maksimal muatan adalah " 
                + Pengiriman.MAX_MUATAN_KG + " kg");
        }

        if (muatanKg <= 0) {
            throw new IllegalArgumentException("Muatan harus lebih dari 0 kg");
        }

        //validasi mandor: harus punya role mandor
        User mandor = userRepository.findById(mandorId)
                .orElseThrow(() -> new IllegalArgumentException("Mandor tidak ditemukan"));
        if (mandor.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException(
                "User dengan id " + mandorId + " bukan seorang Mandor");
        }

        //validasi supir truk ada
        SupirTruk supirTruk = supirTrukRepository.findById(supirTrukId)
                .orElseThrow(() -> new IllegalArgumentException("Supir truk tidak ditemukan"));

        supirTruk.setSedangBertugas(true);
        supirTrukRepository.save(supirTruk);

        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(mandorId)
                .muatanKg(muatanKg)
                .tujuan(tujuan)
                .build();
        return pengirimanRepository.save(pengiriman);
    }

    @Override
    public Pengiriman ubahStatusPengiriman(UUID pengirimanId, UUID supirTrukId, 
                                            StatusPengiriman statusBaru) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        if (!pengiriman.getSupirTrukId().equals(supirTrukId)) {
            throw new IllegalArgumentException(
                "Hanya supir yang ditugaskan yang dapat mengubah status pengiriman");
        }

        validateStatusTransition(pengiriman.getStatus(), statusBaru);

        pengiriman.setStatus(statusBaru);

        if (statusBaru == StatusPengiriman.TIBA) {
            SupirTruk supirTruk = supirTrukRepository.findById(supirTrukId)
                    .orElseThrow(() -> new IllegalArgumentException("Supir truk tidak ditemukan"));
            supirTruk.setSedangBertugas(false);
            supirTrukRepository.save(supirTruk);
        }

        return pengirimanRepository.save(pengiriman);
    }

    private void validateStatusTransition(StatusPengiriman statusSaatIni, 
                                           StatusPengiriman statusBaru) {

        boolean valid = switch (statusSaatIni) {
            case MENUNGGU -> statusBaru == StatusPengiriman.MEMUAT;
            case MEMUAT -> statusBaru == StatusPengiriman.MENGIRIM;
            case MENGIRIM -> statusBaru == StatusPengiriman.TIBA;
            case TIBA -> false;
            case DISETUJUI -> false;
            case DITOLAK -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                "Transisi status tidak valid dari " + statusSaatIni.getDisplayName() 
                + " ke " + statusBaru.getDisplayName());
        }
    }

    @Override
    public List<Pengiriman> getDaftarPengirimanSupir(UUID supirTrukId) {
        return pengirimanRepository.findBySupirTrukId(supirTrukId);
    }

    @Override
    public List<Pengiriman> getRiwayatPengirimanSupir(UUID supirTrukId,
                                                      LocalDate tanggalMulai,
                                                      LocalDate tanggalSelesai) {
        if (tanggalMulai != null && tanggalSelesai != null && tanggalMulai.isAfter(tanggalSelesai)) {
            throw new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai");
        }

        return pengirimanRepository.findRiwayatSupir(supirTrukId, tanggalMulai, tanggalSelesai);
    }

    @Override
    public String getAlasanPenolakan(UUID pengirimanId, UUID supirTrukId) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        if (!pengiriman.getSupirTrukId().equals(supirTrukId)) {
            throw new IllegalArgumentException("Hanya supir yang ditugaskan yang dapat melihat alasan penolakan");
        }

        if (pengiriman.getStatus() != StatusPengiriman.DITOLAK) {
            throw new IllegalArgumentException("Pengiriman tidak berstatus ditolak");
        }

        if (pengiriman.getAlasanPenolakan() == null || pengiriman.getAlasanPenolakan().isBlank()) {
            throw new IllegalArgumentException("Alasan penolakan tidak tersedia");
        }

        return pengiriman.getAlasanPenolakan();
    }

    @Override
    public List<ApprovedPengirimanResponse> getPengirimanDisetujui(String mandorName,
                                                                   LocalDate tanggalMulai,
                                                                   LocalDate tanggalSelesai) {
        if (tanggalMulai != null && tanggalSelesai != null && tanggalMulai.isAfter(tanggalSelesai)) {
            throw new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai");
        }

        String normalizedMandorQuery = mandorName == null ? "" : mandorName.trim().toLowerCase();

        return pengirimanAssignmentRepository.findAll().stream()
                .filter(a -> a.getApproval() == ApprovalAssignment.APPROVED)
                .filter(a -> {
                    if (normalizedMandorQuery.isBlank()) {
                        return true;
                    }
                    String mandorEmail = a.getMandorEmail() == null ? "" : a.getMandorEmail().toLowerCase();
                    return mandorEmail.contains(normalizedMandorQuery);
                })
                .filter(a -> {
                    if (tanggalMulai == null && tanggalSelesai == null) {
                        return true;
                    }
                    if (a.getCreatedAt() == null) {
                        return false;
                    }
                    LocalDate tanggal = a.getCreatedAt().toLocalDate();
                    boolean afterStart = tanggalMulai == null || !tanggal.isBefore(tanggalMulai);
                    boolean beforeEnd = tanggalSelesai == null || !tanggal.isAfter(tanggalSelesai);
                    return afterStart && beforeEnd;
                })
                .map(this::toApprovedResponseFromAssignment)
                .collect(Collectors.toList());
    }

    private ApprovedPengirimanResponse toApprovedResponseFromAssignment(PengirimanAssignment assignment) {
        User mandor = userRepository.findByUsername(assignment.getMandorEmail()).orElse(null);
        String mandorFullname = mandor != null && mandor.getFullname() != null
                ? mandor.getFullname().trim()
                : "";
        String mandorDisplayName = !mandorFullname.isBlank()
                ? mandorFullname
                : assignment.getMandorEmail();

        return new ApprovedPengirimanResponse(
                UUID.nameUUIDFromBytes(("assignment-" + assignment.getId()).getBytes()),
                UUID.nameUUIDFromBytes(assignment.getSupirEmail().getBytes()),
                mandor != null ? mandor.getId() : null,
                mandorDisplayName,
                assignment.getMuatanKg(),
                assignment.getTujuan(),
                assignment.getCreatedAt(),
                StatusPengiriman.DISETUJUI
        );
    }

    private User validateAdmin(Long adminId) {
        if (adminId == null) {
            throw new IllegalArgumentException("Admin tidak ditemukan");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin tidak ditemukan"));
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("User dengan id " + adminId + " bukan seorang Admin");
        }
        return admin;
    }

    private void validateAdminDecision(Pengiriman pengiriman) {
        if (pengiriman.getStatus() != StatusPengiriman.TIBA) {
            throw new IllegalArgumentException("Pengiriman belum sampai tujuan");
        }
    }

    private String normalizeAlasanPenolakan(String alasanPenolakan) {
        if (alasanPenolakan == null || alasanPenolakan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi");
        }
        return alasanPenolakan.trim();
    }

    private void validateMuatanDiakui(double muatanKgDiakui, double muatanKg) {
        if (muatanKgDiakui <= 0) {
            throw new IllegalArgumentException("Kilogram diakui harus lebih dari 0 kg");
        }
        if (muatanKgDiakui >= muatanKg) {
            throw new IllegalArgumentException("Kilogram diakui harus lebih kecil dari muatan");
        }
    }

    @Override
    public Pengiriman setujuiPengiriman(UUID pengirimanId, Long mandorId) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        if (mandorId == null) {
            throw new IllegalArgumentException("Mandor tidak ditemukan");
        }

        User mandor = userRepository.findById(mandorId)
                .orElseThrow(() -> new IllegalArgumentException("Mandor tidak ditemukan"));
        if (mandor.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException("User dengan id " + mandorId + " bukan seorang Mandor");
        }

        if (!pengiriman.getMandorId().equals(mandorId)) {
            throw new IllegalArgumentException("Mandor tidak berhak menyetujui pengiriman ini");
        }

        if (pengiriman.getStatus() != StatusPengiriman.TIBA) {
            throw new IllegalArgumentException("Pengiriman belum sampai tujuan");
        }

        pengiriman.setStatus(StatusPengiriman.DISETUJUI);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman);
        return savedPengiriman;
    }

    @Override
    public Pengiriman tolakPengiriman(UUID pengirimanId, Long mandorId, String alasanPenolakan) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        if (mandorId == null) {
            throw new IllegalArgumentException("Mandor tidak ditemukan");
        }

        if (alasanPenolakan == null || alasanPenolakan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi");
        }

        User mandor = userRepository.findById(mandorId)
                .orElseThrow(() -> new IllegalArgumentException("Mandor tidak ditemukan"));
        if (mandor.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException("User dengan id " + mandorId + " bukan seorang Mandor");
        }

        if (!pengiriman.getMandorId().equals(mandorId)) {
            throw new IllegalArgumentException("Mandor tidak berhak menolak pengiriman ini");
        }

        if (pengiriman.getStatus() != StatusPengiriman.TIBA) {
            throw new IllegalArgumentException("Pengiriman belum sampai tujuan");
        }

        pengiriman.setAlasanPenolakan(alasanPenolakan.trim());
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        return pengirimanRepository.save(pengiriman);
    }

    @Override
    public Pengiriman setujuiPengirimanAdmin(UUID pengirimanId, Long adminId) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        validateAdmin(adminId);
        validateAdminDecision(pengiriman);

        pengiriman.setStatus(StatusPengiriman.DISETUJUI);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman);
        return savedPengiriman;
    }

    @Override
    public Pengiriman tolakPengirimanAdmin(UUID pengirimanId, Long adminId, String alasanPenolakan) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        validateAdmin(adminId);
        validateAdminDecision(pengiriman);

        String normalizedReason = normalizeAlasanPenolakan(alasanPenolakan);
        pengiriman.setAlasanPenolakan(normalizedReason);
        pengiriman.setMuatanKgDiakui(null);
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        return pengirimanRepository.save(pengiriman);
    }

    @Override
    public Pengiriman tolakPengirimanParsialAdmin(UUID pengirimanId,
                                                  Long adminId,
                                                  double muatanKgDiakui,
                                                  String alasanPenolakan) {
        Pengiriman pengiriman = pengirimanRepository.findById(pengirimanId)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));

        validateAdmin(adminId);
        validateAdminDecision(pengiriman);
        validateMuatanDiakui(muatanKgDiakui, pengiriman.getMuatanKg());

        String normalizedReason = normalizeAlasanPenolakan(alasanPenolakan);
        pengiriman.setAlasanPenolakan(normalizedReason);
        pengiriman.setMuatanKgDiakui(muatanKgDiakui);
        pengiriman.setStatus(StatusPengiriman.DITOLAK);
        Pengiriman savedPengiriman = pengirimanRepository.save(pengiriman);
        payrollRequestSender.sendPayrollRequest(savedPengiriman, muatanKgDiakui);
        return savedPengiriman;
    }

    @Override
    public List<Pengiriman> getDaftarPengirimanBerlangsung() {
        return pengirimanRepository.findAllSedangBerlangsung();
    }

    @Override
    public Pengiriman getPengirimanById(UUID id) {
        return pengirimanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman tidak ditemukan"));
    }

    @Override
    public List<Pengiriman> getAllPengiriman() {
        return pengirimanRepository.findAll();
    }
}
