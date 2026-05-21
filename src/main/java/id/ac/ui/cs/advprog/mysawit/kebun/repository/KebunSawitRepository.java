package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;

import java.util.List;
import java.util.Optional;

public interface KebunSawitRepository {
    KebunSawit save(KebunSawit kebun);
    Optional<KebunSawit> findById(String id);
    Optional<KebunSawit> findByKodeUnik(String kodeUnik);
    List<KebunSawit> findAll();
    List<KebunSawit> search(String searchNama, String searchKode);
    void deleteById(String id);
}
