package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunValidationException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Component;

@Component
public class KebunValidator {

    private static final String KODE_UNIK_PATTERN = "^[A-Z]{2}-\\d{4}$";

    private final KebunSawitRepository repository;
    private final KebunGeometry geometry;

    public KebunValidator(KebunSawitRepository repository, KebunGeometry geometry) {
        this.repository = repository;
        this.geometry = geometry;
    }

    public void validateCreate(KebunSawit kebun) {
        validateKodeUnik(kebun);
        rejectDuplicateKode(kebun);
        validateKebunShape(kebun);
        rejectOverlap(kebun, null);
    }

    public void validateUpdate(String id, KebunSawit kebun) {
        validateKebunShape(kebun);
        rejectOverlap(kebun, id);
    }

    private void validateKodeUnik(KebunSawit kebun) {
        if (kebun.getKodeUnik() == null || !kebun.getKodeUnik().matches(KODE_UNIK_PATTERN)) {
            throw new KebunValidationException(
                    "Format kode unik tidak valid. Gunakan format: XX-0000 (contoh: KB-0001)");
        }
    }

    private void rejectDuplicateKode(KebunSawit kebun) {
        if (repository.findByKodeUnik(kebun.getKodeUnik()).isPresent()) {
            throw new KebunConflictException("Kode unik kebun sudah digunakan: " + kebun.getKodeUnik());
        }
    }

    private void validateKebunShape(KebunSawit kebun) {
        if (kebun.getNamaKebun() == null) {
            throw new KebunValidationException("Nama kebun tidak boleh null");
        }

        if (kebun.getKiriAtas() == null || kebun.getKiriBawah() == null
                || kebun.getKananAtas() == null || kebun.getKananBawah() == null) {
            throw new KebunValidationException("Semua 4 koordinat harus diisi");
        }

        if (!geometry.isAxisAlignedSquare(kebun)) {
            throw new KebunValidationException("Keempat koordinat yang dimasukkan tidak membentuk persegi sempurna");
        }
    }

    private void rejectOverlap(KebunSawit kebun, String ignoredKebunId) {
        for (KebunSawit existing : repository.findAll()) {
            if (ignoredKebunId != null && ignoredKebunId.equals(existing.getId())) {
                continue;
            }

            if (OverlapValidator.isOverlapping(kebun.getKoordinatAsList(), existing.getKoordinatAsList())) {
                throw new KebunValidationException(
                        "Kebun overlap dengan kebun: " + existing.getNamaKebun()
                                + " (" + existing.getKodeUnik() + ")");
            }
        }
    }
}
