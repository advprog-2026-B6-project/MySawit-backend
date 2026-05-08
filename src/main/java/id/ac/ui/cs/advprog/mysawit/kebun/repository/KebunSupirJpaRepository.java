package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KebunSupirJpaRepository extends JpaRepository<KebunSupirEntity, String> {
    List<KebunSupirEntity> findAllByKebunId(String kebunId);
    Optional<KebunSupirEntity> findBySupirId(Long supirId);
    boolean existsByKebunId(String kebunId);
    boolean existsBySupirId(Long supirId);
}
