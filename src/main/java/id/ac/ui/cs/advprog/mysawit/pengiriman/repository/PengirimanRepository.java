package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PengirimanRepository {
    Pengiriman save(Pengiriman pengiriman);
    Optional<Pengiriman> findById(UUID id);
    List<Pengiriman> findAll();
    List<Pengiriman> findBySupirTrukId(UUID supirTrukId);
    List<Pengiriman> findAllSedangBerlangsung();
    void deleteById(UUID id);
}
