package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KebunSawitJpaRepository extends JpaRepository<KebunSawitEntity, String> {
    Optional<KebunSawitEntity> findByKodeUnik(String kodeUnik);
}