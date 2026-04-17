package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SupirTrukRepositoryImpl implements SupirTrukRepository {
    private final Map<UUID, SupirTruk> supirTrukMap = new HashMap<>();

    @Override
    public SupirTruk save(SupirTruk supirTruk) {
        supirTrukMap.put(supirTruk.getId(), supirTruk);
        return supirTruk;
    }

    @Override
    public Optional<SupirTruk> findById(UUID id) {
        return Optional.ofNullable(supirTrukMap.get(id));
    }

    @Override
    public List<SupirTruk> findAll() {
        return new ArrayList<>(supirTrukMap.values());
    }

    @Override
    public List<SupirTruk> findAllBertugas() {
        return supirTrukMap.values().stream()
                .filter(SupirTruk::isSedangBertugas)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        supirTrukMap.remove(id);
    }
}
