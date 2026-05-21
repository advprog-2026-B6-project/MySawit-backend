package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KebunPlacementGuardImpl implements KebunPlacementGuard {

    private final KebunMandorJpaRepository kebunMandorRepository;
    private final KebunSupirJpaRepository kebunSupirRepository;

    public KebunPlacementGuardImpl(KebunMandorJpaRepository kebunMandorRepository,
                                   KebunSupirJpaRepository kebunSupirRepository) {
        this.kebunMandorRepository = kebunMandorRepository;
        this.kebunSupirRepository = kebunSupirRepository;
    }

    @Override
    public boolean isMandorPlaced(Long mandorId) {
        return kebunMandorRepository.existsByMandorId(mandorId);
    }

    @Override
    public boolean isSupirPlaced(Long supirId) {
        return kebunSupirRepository.existsBySupirId(supirId);
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
        return kebunMandorRepository.findByMandorId(mandorId)
                .map(entity -> entity.getKebunId());
    }

    @Override
    public Optional<String> getKebunIdBySupirId(Long supirId) {
        return kebunSupirRepository.findBySupirId(supirId)
                .map(entity -> entity.getKebunId());
    }
}
