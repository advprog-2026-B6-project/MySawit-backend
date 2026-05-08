package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;

@Repository
@Primary
public class PostgresHasilRepository implements HasilRepository {
    private final HasilJpaRepository jpaRepository;

    public PostgresHasilRepository(HasilJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Hasil save(Hasil report) {
        HasilEntity entity = toEntity(report);
        HasilEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Hasil> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Hasil> findByWorkerIdAndDate(String workerId, LocalDate hasilDate) {
        return jpaRepository.findByWorkerIdAndHasilDate(workerId, hasilDate)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByWorkerIdAndDate(String workerId, LocalDate hasilDate) {
        return jpaRepository.existsByWorkerIdAndHasilDate(workerId, hasilDate);
    }

    private HasilEntity toEntity(Hasil report) {
        return HasilEntity.from(report);
    }

    private Hasil toDomain(HasilEntity entity) {
        return Hasil.of(
            entity.getId(),
            entity.getWorkerId(),
            entity.getHasilDate(),
            entity.getWeightKg(),
            entity.getNews(),
            entity.getPhotoUrls(),
            entity.isLocked(),
            entity.getStatus() == null ? HasilStatus.SUBMITTED : entity.getStatus()
        );
    }
}
