package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;

import java.util.List;
import java.util.Optional;

public interface KebunSawitService {
    // Command Operations
    KebunSawit create(KebunSawit kebun);
    KebunSawit update(String id, KebunSawit updatedKebun);
    void delete(String id);

    // Query Operations
    List<KebunSawit> findAll(String searchNama, String searchKode);
    Optional<KebunSawit> findByKodeUnik(String kodeUnik);
    KebunDetailResponse getDetail(String kebunId, String searchSupirNama);
}
