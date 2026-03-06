package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;

import java.util.List;
import java.util.Optional;

public interface KebunSawitService {
    KebunSawit create(KebunSawit kebun);
    List<KebunSawit> findAll(String searchNama, String searchKode);
    Optional<KebunSawit> findByKodeUnik(String kodeUnik);
}
