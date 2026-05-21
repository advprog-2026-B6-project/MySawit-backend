package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryKebunSawitRepository implements KebunSawitRepository {

    private final Map<String, KebunSawit> kebunRepository = new ConcurrentHashMap<>();

    @Override
    public KebunSawit save(KebunSawit kebun) {
        kebunRepository.put(kebun.getId(), kebun);
        return kebun;
    }

    @Override
    public Optional<KebunSawit> findByKodeUnik(String kodeUnik) {
        return kebunRepository.values().stream()
                .filter(k -> k.getKodeUnik().equals(kodeUnik))
                .findFirst();
    }

    @Override
    public Optional<KebunSawit> findById(String id) {
        return Optional.ofNullable(kebunRepository.get(id));
    }

    @Override
    public List<KebunSawit> findAll() {
        return new ArrayList<>(kebunRepository.values());
    }

    @Override
    public List<KebunSawit> search(String searchNama, String searchKode) {
        String nama = searchNama == null ? "" : searchNama.toLowerCase();
        String kode = searchKode == null ? "" : searchKode.toLowerCase();

        return kebunRepository.values().stream()
                .filter(k -> nama.isEmpty() || k.getNamaKebun().toLowerCase().contains(nama))
                .filter(k -> kode.isEmpty() || k.getKodeUnik().toLowerCase().contains(kode))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        kebunRepository.remove(id);
    }
}
