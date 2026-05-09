package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PengirimanServiceImpl implements PengirimanService {

    private final PengirimanRepository pengirimanRepository;
    private final SupirTrukRepository supirTrukRepository;
    private final UserRepository userRepository;

    public PengirimanServiceImpl(PengirimanRepository pengirimanRepository,
                                  SupirTrukRepository supirTrukRepository,
                                  UserRepository userRepository) {
        this.pengirimanRepository = pengirimanRepository;
        this.supirTrukRepository = supirTrukRepository;
        this.userRepository = userRepository;
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
        return pengirimanRepository.save(pengiriman);
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
