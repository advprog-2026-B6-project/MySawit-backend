package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KebunSawitJpaRepository extends JpaRepository<KebunSawitEntity, String> {
    Optional<KebunSawitEntity> findByKodeUnik(String kodeUnik);

    @Query("""
            SELECT kebun FROM KebunSawitEntity kebun
            WHERE LOWER(kebun.namaKebun) LIKE LOWER(CONCAT('%', :nama, '%'))
            AND LOWER(kebun.kodeUnik) LIKE LOWER(CONCAT('%', :kode, '%'))
            """)
    List<KebunSawitEntity> search(@Param("nama") String searchNama, @Param("kode") String searchKode);
}
