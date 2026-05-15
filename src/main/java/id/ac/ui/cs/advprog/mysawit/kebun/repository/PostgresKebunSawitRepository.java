package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class PostgresKebunSawitRepository implements KebunSawitRepository {

    private final KebunSawitJpaRepository jpaRepository;
    private final KebunSawitEntityMapper mapper;

    public PostgresKebunSawitRepository(KebunSawitJpaRepository jpaRepository,
                                        KebunSawitEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public KebunSawit save(KebunSawit kebun) {
        KebunSawitEntity entity = mapper.toEntity(kebun);
        KebunSawitEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<KebunSawit> findByKodeUnik(String kodeUnik) {
        return jpaRepository.findByKodeUnik(kodeUnik)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<KebunSawit> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<KebunSawit> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KebunSawit> search(String searchNama, String searchKode) {
        return jpaRepository.search(normalizeSearch(searchNama), normalizeSearch(searchKode)).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    private String normalizeSearch(String searchValue) {
        return searchValue == null ? "" : searchValue;
    }
}
