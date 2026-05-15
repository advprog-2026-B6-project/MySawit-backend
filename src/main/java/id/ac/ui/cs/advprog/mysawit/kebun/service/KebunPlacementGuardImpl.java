package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KebunPlacementGuardImpl implements KebunPlacementGuard {

    private final KebunAssignmentRepository assignmentRepository;

    public KebunPlacementGuardImpl(KebunAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public boolean isMandorPlaced(Long mandorId) {
        return assignmentRepository.mandorIsAssigned(mandorId);
    }

    @Override
    public boolean isSupirPlaced(Long supirId) {
        return assignmentRepository.supirIsAssigned(supirId);
    }

    @Override
    public boolean areInSameKebun(Long mandorId, Long supirId) {
        Optional<String> mandorKebunId = getKebunIdByMandorId(mandorId);
        Optional<String> supirKebunId = getKebunIdBySupirId(supirId);

        if (mandorKebunId.isEmpty() || supirKebunId.isEmpty()) {
            return false;
        }

        return mandorKebunId.get().equals(supirKebunId.get());
    }

    @Override
    public Optional<String> getKebunIdByMandorId(Long mandorId) {
        return assignmentRepository.findKebunIdByMandorId(mandorId);
    }

    @Override
    public Optional<String> getKebunIdBySupirId(Long supirId) {
        return assignmentRepository.findKebunIdBySupirId(supirId);
    }
}
