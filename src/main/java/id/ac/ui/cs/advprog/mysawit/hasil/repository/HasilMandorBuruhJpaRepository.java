package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HasilMandorBuruhJpaRepository extends JpaRepository<HasilMandorBuruhEntity, String> {
    List<HasilMandorBuruhEntity> findAllByMandorId(Long mandorId);

    Optional<HasilMandorBuruhEntity> findByMandorIdAndBuruhId(Long mandorId, Long buruhId);

    boolean existsByMandorIdAndBuruhId(Long mandorId, Long buruhId);
}
