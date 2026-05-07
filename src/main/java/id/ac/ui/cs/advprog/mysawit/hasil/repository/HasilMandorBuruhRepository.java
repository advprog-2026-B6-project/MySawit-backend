package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.util.List;

public interface HasilMandorBuruhRepository {
    HasilMandorBuruhEntity save(HasilMandorBuruhEntity assignment);

    List<Long> findBuruhIdsByMandorId(Long mandorId);

    boolean existsByMandorIdAndBuruhId(Long mandorId, Long buruhId);
}
