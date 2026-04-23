package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HasilJpaRepository extends JpaRepository<HasilEntity, String> {
    Optional<HasilEntity> findByWorkerIdAndHasilDate(String workerId, LocalDate hasilDate);

    boolean existsByWorkerIdAndHasilDate(String workerId, LocalDate hasilDate);
}
