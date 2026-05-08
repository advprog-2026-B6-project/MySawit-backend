package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KebunMandorJpaRepository extends JpaRepository<KebunMandorEntity, String> {
    Optional<KebunMandorEntity> findByKebunId(String kebunId);
    Optional<KebunMandorEntity> findByMandorId(Long mandorId);
    boolean existsByKebunId(String kebunId);
    boolean existsByMandorId(Long mandorId);
}
