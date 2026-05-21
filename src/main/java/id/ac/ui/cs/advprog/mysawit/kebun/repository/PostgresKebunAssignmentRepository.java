package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresKebunAssignmentRepository implements KebunAssignmentRepository {

    private final KebunMandorJpaRepository kebunMandorRepository;
    private final KebunSupirJpaRepository kebunSupirRepository;

    public PostgresKebunAssignmentRepository(KebunMandorJpaRepository kebunMandorRepository,
                                             KebunSupirJpaRepository kebunSupirRepository) {
        this.kebunMandorRepository = kebunMandorRepository;
        this.kebunSupirRepository = kebunSupirRepository;
    }

    @Override
    public Optional<Long> findMandorIdByKebunId(String kebunId) {
        return kebunMandorRepository.findByKebunId(kebunId)
                .map(KebunMandorEntity::getMandorId);
    }

    @Override
    public Optional<String> findKebunIdByMandorId(Long mandorId) {
        return kebunMandorRepository.findByMandorId(mandorId)
                .map(KebunMandorEntity::getKebunId);
    }

    @Override
    public boolean kebunHasMandor(String kebunId) {
        return kebunMandorRepository.existsByKebunId(kebunId);
    }

    @Override
    public boolean mandorIsAssigned(Long mandorId) {
        return kebunMandorRepository.existsByMandorId(mandorId);
    }

    @Override
    public void assignMandor(String kebunId, Long mandorId) {
        KebunMandorEntity assignment = new KebunMandorEntity();
        assignment.setKebunId(kebunId);
        assignment.setMandorId(mandorId);
        kebunMandorRepository.save(assignment);
    }

    @Override
    public void moveMandor(Long mandorId, String fromKebunId, String toKebunId) {
        KebunMandorEntity currentAssignment = kebunMandorRepository.findByMandorId(mandorId)
                .orElseThrow(() -> new IllegalStateException("Mandor assignment not found"));
        if (!currentAssignment.getKebunId().equals(fromKebunId)) {
            throw new IllegalStateException("Mandor assignment source kebun mismatch");
        }
        kebunMandorRepository.delete(currentAssignment);
        assignMandor(toKebunId, mandorId);
    }

    @Override
    public List<Long> findSupirIdsByKebunId(String kebunId) {
        return kebunSupirRepository.findAllByKebunId(kebunId).stream()
                .map(KebunSupirEntity::getSupirId)
                .toList();
    }

    @Override
    public Optional<String> findKebunIdBySupirId(Long supirId) {
        return kebunSupirRepository.findBySupirId(supirId)
                .map(KebunSupirEntity::getKebunId);
    }

    @Override
    public boolean supirIsAssigned(Long supirId) {
        return kebunSupirRepository.existsBySupirId(supirId);
    }

    @Override
    public void assignSupir(String kebunId, Long supirId) {
        KebunSupirEntity assignment = new KebunSupirEntity();
        assignment.setKebunId(kebunId);
        assignment.setSupirId(supirId);
        kebunSupirRepository.save(assignment);
    }

    @Override
    public void moveSupir(Long supirId, String fromKebunId, String toKebunId) {
        KebunSupirEntity currentAssignment = kebunSupirRepository.findBySupirId(supirId)
                .orElseThrow(() -> new IllegalStateException("Supir assignment not found"));
        if (!currentAssignment.getKebunId().equals(fromKebunId)) {
            throw new IllegalStateException("Supir assignment source kebun mismatch");
        }
        kebunSupirRepository.delete(currentAssignment);
        assignSupir(toKebunId, supirId);
    }
}
