package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;

public interface KebunCommandService {
    KebunSawit create(KebunSawit kebun);
    KebunSawit update(String id, KebunSawit updatedKebun);
    void delete(String id);
}
