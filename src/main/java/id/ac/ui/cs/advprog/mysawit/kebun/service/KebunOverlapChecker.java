package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunValidationException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Component;

@Component
public class KebunOverlapChecker {

    private final KebunSawitRepository repository;

    public KebunOverlapChecker(KebunSawitRepository repository) {
        this.repository = repository;
    }

    public void rejectOverlap(KebunSawit candidate, String ignoredKebunId) {
        for (KebunSawit existing : repository.findAll()) {
            if (shouldIgnore(existing, ignoredKebunId)) {
                continue;
            }

            if (OverlapValidator.isOverlapping(candidate.getKoordinatAsList(), existing.getKoordinatAsList())) {
                throw new KebunValidationException(
                        "Kebun overlap dengan kebun: " + existing.getNamaKebun()
                                + " (" + existing.getKodeUnik() + ")");
            }
        }
    }

    private boolean shouldIgnore(KebunSawit existing, String ignoredKebunId) {
        return ignoredKebunId != null && ignoredKebunId.equals(existing.getId());
    }
}
