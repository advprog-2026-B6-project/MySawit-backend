package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class PostgresKebunSawitRepository implements KebunSawitRepository {

    private final KebunSawitJpaRepository jpaRepository;

    public PostgresKebunSawitRepository(KebunSawitJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public KebunSawit save(KebunSawit kebun) {
        KebunSawitEntity entity = toEntity(kebun);
        KebunSawitEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<KebunSawit> findByKodeUnik(String kodeUnik) {
        return jpaRepository.findByKodeUnik(kodeUnik)
                .map(this::toDomain);
    }

    @Override
    public Optional<KebunSawit> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<KebunSawit> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    private KebunSawitEntity toEntity(KebunSawit domain) {
        return new KebunSawitEntity(
                domain.getId(),
                domain.getNamaKebun(),
                domain.getKodeUnik(),
                domain.getLuasHektare(),
                toEntityCoord(domain.getKiriAtas()),
                toEntityCoord(domain.getKiriBawah()),
                toEntityCoord(domain.getKananAtas()),
                toEntityCoord(domain.getKananBawah())
        );
    }

    private KebunSawit toDomain(KebunSawitEntity entity) {
        return new KebunSawit(
                entity.getId(),
                entity.getNamaKebun(),
                entity.getKodeUnik(),
                entity.getLuasHektare(),
                toDomainCoord(entity.getKiriAtas()),
                toDomainCoord(entity.getKiriBawah()),
                toDomainCoord(entity.getKananAtas()),
                toDomainCoord(entity.getKananBawah())
        );
    }

    private CoordinateEmbeddable toEntityCoord(Coordinate domainCoord) {
        if (domainCoord == null) return null;
        return new CoordinateEmbeddable(domainCoord.getX(), domainCoord.getY());
    }

    private Coordinate toDomainCoord(CoordinateEmbeddable entityCoord) {
        if (entityCoord == null) return null;
        return new Coordinate(entityCoord.getX(), entityCoord.getY());
    }
}