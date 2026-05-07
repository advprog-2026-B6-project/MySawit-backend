package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class PostgresHasilMandorBuruhRepository implements HasilMandorBuruhRepository {
    private final HasilMandorBuruhJpaRepository jpaRepository;

    public PostgresHasilMandorBuruhRepository(HasilMandorBuruhJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HasilMandorBuruhEntity save(HasilMandorBuruhEntity assignment) {
        return jpaRepository.save(assignment);
    }

    @Override
    public List<Long> findBuruhIdsByMandorId(Long mandorId) {
        return jpaRepository.findAllByMandorId(mandorId).stream()
                .map(HasilMandorBuruhEntity::getBuruhId)
                .toList();
    }

    @Override
    public boolean existsByMandorIdAndBuruhId(Long mandorId, Long buruhId) {
        return jpaRepository.existsByMandorIdAndBuruhId(mandorId, buruhId);
    }
}
