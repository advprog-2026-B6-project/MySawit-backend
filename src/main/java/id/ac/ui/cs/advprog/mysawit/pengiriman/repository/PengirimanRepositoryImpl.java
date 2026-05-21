package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

@Repository
public class PengirimanRepositoryImpl implements PengirimanRepository {
    private final Map<UUID, Pengiriman> pengirimanMap = new HashMap<>();

    @Override
    public Pengiriman save(Pengiriman pengiriman) {
        pengirimanMap.put(pengiriman.getId(), pengiriman);
        return pengiriman;
    }

    @Override
    public Optional<Pengiriman> findById(UUID id) {
        return Optional.ofNullable(pengirimanMap.get(id));
    }

    @Override
    public List<Pengiriman> findAll() {
        return new ArrayList<>(pengirimanMap.values());
    }

    @Override
    public List<Pengiriman> findBySupirTrukId(UUID supirTrukId) {
        return pengirimanMap.values().stream()
                .filter(p -> p.getSupirTrukId().equals(supirTrukId))
                .toList();
    }

    @Override
    public List<Pengiriman> findRiwayatSupir(UUID supirTrukId,
                                             LocalDate tanggalMulai,
                                             LocalDate tanggalSelesai) {
        return pengirimanMap.values().stream()
                .filter(p -> p.getSupirTrukId().equals(supirTrukId))
        .filter(p -> p.getStatus() == StatusPengiriman.TIBA
            || p.getStatus() == StatusPengiriman.DISETUJUI
            || p.getStatus() == StatusPengiriman.DITOLAK)
                .filter(p -> {
                    LocalDate tanggal = p.getWaktuDibuat().toLocalDate();
                    boolean afterStart = tanggalMulai == null || !tanggal.isBefore(tanggalMulai);
                    boolean beforeEnd = tanggalSelesai == null || !tanggal.isAfter(tanggalSelesai);
                    return afterStart && beforeEnd;
                })
                .toList();
    }

    @Override
    public List<Pengiriman> findAllSedangBerlangsung() {
        return pengirimanMap.values().stream()
                .filter(Pengiriman::isSedangBerlangsung)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        pengirimanMap.remove(id);
    }
}
