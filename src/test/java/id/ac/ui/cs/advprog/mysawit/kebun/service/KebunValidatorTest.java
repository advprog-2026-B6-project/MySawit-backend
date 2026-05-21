package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunValidationException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunValidatorTest {

    @Mock
    private KebunSawitRepository repository;

    private KebunValidator validator;

    @BeforeEach
    void setUp() {
        KebunGeometry geometry = new KebunGeometry();
        KebunOverlapChecker overlapChecker = new KebunOverlapChecker(repository);
        validator = new KebunValidator(repository, geometry, overlapChecker);
    }

    private KebunSawit createValidKebun(String id, String kode) {
        KebunSawit kebun = new KebunSawit();
        kebun.setId(id);
        kebun.setNamaKebun("Kebun " + kode);
        kebun.setKodeUnik(kode);
        kebun.setKiriAtas(new Coordinate(0, 200));
        kebun.setKiriBawah(new Coordinate(0, 0));
        kebun.setKananAtas(new Coordinate(200, 200));
        kebun.setKananBawah(new Coordinate(200, 0));
        return kebun;
    }

    @Nested
    class ValidateCreateTests {

        @Test
        void validKebun_shouldNotThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());
            when(repository.findAll()).thenReturn(List.of());

            assertDoesNotThrow(() -> validator.validateCreate(kebun));
        }

        @Test
        void invalidKodeFormat_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "INVALID");
            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullKode_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKodeUnik(null);
            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void lowercaseKode_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "kb-0001");
            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void duplicateKode_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.of(kebun));

            assertThrows(KebunConflictException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullNama_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setNamaKebun(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullKiriAtas_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKiriAtas(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullKiriBawah_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKiriBawah(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullKananAtas_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKananAtas(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nullKananBawah_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKananBawah(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void nonSquareShape_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            // Make it a rectangle
            kebun.setKananAtas(new Coordinate(300, 200));
            kebun.setKananBawah(new Coordinate(300, 0));
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(kebun));
        }

        @Test
        void overlappingKebun_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001");
            KebunSawit newKebun = createValidKebun(null, "KB-0002");
            // same coordinates → overlap
            when(repository.findByKodeUnik("KB-0002")).thenReturn(Optional.empty());
            when(repository.findAll()).thenReturn(List.of(existing));

            assertThrows(KebunValidationException.class, () -> validator.validateCreate(newKebun));
        }
    }

    @Nested
    class ValidateUpdateTests {

        @Test
        void validUpdate_shouldNotThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            when(repository.findAll()).thenReturn(List.of());

            assertDoesNotThrow(() -> validator.validateUpdate("id-1", kebun));
        }

        @Test
        void nullNamaOnUpdate_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setNamaKebun(null);

            assertThrows(KebunValidationException.class, () -> validator.validateUpdate("id-1", kebun));
        }

        @Test
        void nullCoordsOnUpdate_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKiriAtas(null);

            assertThrows(KebunValidationException.class, () -> validator.validateUpdate("id-1", kebun));
        }

        @Test
        void nonSquareOnUpdate_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001");
            kebun.setKananAtas(new Coordinate(300, 200));
            kebun.setKananBawah(new Coordinate(300, 0));

            assertThrows(KebunValidationException.class, () -> validator.validateUpdate("id-1", kebun));
        }

        @Test
        void selfOverlap_shouldNotThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001");
            KebunSawit updated = createValidKebun(null, "KB-0001");
            when(repository.findAll()).thenReturn(List.of(existing));

            // Should ignore self during overlap check
            assertDoesNotThrow(() -> validator.validateUpdate("id-1", updated));
        }

        @Test
        void overlapWithOtherOnUpdate_shouldThrow() {
            KebunSawit other = createValidKebun("id-2", "KB-0002");
            KebunSawit updated = createValidKebun(null, "KB-0001");
            // same coords as other → overlap
            when(repository.findAll()).thenReturn(List.of(other));

            assertThrows(KebunValidationException.class, () -> validator.validateUpdate("id-1", updated));
        }
    }
}
