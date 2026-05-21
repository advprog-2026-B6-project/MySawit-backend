package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import java.util.List;
import java.util.Optional;

public interface KebunAssignmentRepository {

    Optional<Long> findMandorIdByKebunId(String kebunId);
    Optional<String> findKebunIdByMandorId(Long mandorId);
    boolean kebunHasMandor(String kebunId);
    boolean mandorIsAssigned(Long mandorId);
    void assignMandor(String kebunId, Long mandorId);
    void moveMandor(Long mandorId, String fromKebunId, String toKebunId);
    List<Long> findSupirIdsByKebunId(String kebunId);
    Optional<String> findKebunIdBySupirId(Long supirId);
    boolean supirIsAssigned(Long supirId);
    void assignSupir(String kebunId, Long supirId);
    void moveSupir(Long supirId, String fromKebunId, String toKebunId);
}
