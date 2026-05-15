package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("test")
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
        String normalizedNama = normalizeSearch(searchNama);
        String normalizedKode = normalizeSearch(searchKode);

        return kebunRepository.values().stream()
                .filter(kebun -> kebun.getNamaKebun().toLowerCase().contains(normalizedNama))
                .filter(kebun -> kebun.getKodeUnik().toLowerCase().contains(normalizedKode))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        kebunRepository.remove(id);
    }

    private String normalizeSearch(String searchValue) {
        return searchValue == null ? "" : searchValue.toLowerCase();
    }
}
