package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupirTrukRepository {
    SupirTruk save(SupirTruk supirTruk);
    Optional<SupirTruk> findById(UUID id);
    List<SupirTruk> findAll();
    List<SupirTruk> findAllBertugas();
    void deleteById(UUID id);
}
