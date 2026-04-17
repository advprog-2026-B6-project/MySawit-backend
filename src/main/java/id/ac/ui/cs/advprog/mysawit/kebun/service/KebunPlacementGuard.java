package id.ac.ui.cs.advprog.mysawit.kebun.service;

import java.util.Optional;

public interface KebunPlacementGuard {
    boolean isMandorPlaced(Long mandorId);
    boolean isSupirPlaced(Long supirId);
    boolean areInSameKebun(Long mandorId, Long supirId);
    Optional<String> getKebunIdByMandorId(Long mandorId);
    Optional<String> getKebunIdBySupirId(Long supirId);
}
