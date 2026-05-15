package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;

import java.util.List;
import java.util.Optional;

public interface KebunQueryService {
    List<KebunSawit> findAll(String searchNama, String searchKode);
    Optional<KebunSawit> findByKodeUnik(String kodeUnik);
    KebunDetailResponse getDetail(String kebunId, String searchSupirNama);
}
