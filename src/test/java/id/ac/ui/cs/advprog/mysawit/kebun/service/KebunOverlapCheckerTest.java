package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunValidationException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunOverlapCheckerTest {

    @Mock
    private KebunSawitRepository repository;

    @InjectMocks
    private KebunOverlapChecker overlapChecker;

    private KebunSawit createKebun(String id, String kode, double x, double y, double size) {
        KebunSawit kebun = new KebunSawit();
        kebun.setId(id);
        kebun.setNamaKebun("Kebun " + kode);
        kebun.setKodeUnik(kode);
        kebun.setKiriAtas(new Coordinate(x, y + size));
        kebun.setKiriBawah(new Coordinate(x, y));
        kebun.setKananAtas(new Coordinate(x + size, y + size));
        kebun.setKananBawah(new Coordinate(x + size, y));
        return kebun;
    }

    @Test
    void noExistingKebun_shouldNotThrow() {
        KebunSawit candidate = createKebun(null, "KB-0001", 0, 0, 200);
        when(repository.findAll()).thenReturn(List.of());
        assertDoesNotThrow(() -> overlapChecker.rejectOverlap(candidate, null));
    }

    @Test
    void noOverlap_shouldNotThrow() {
        KebunSawit existing = createKebun("id-1", "KB-0001", 0, 0, 200);
        KebunSawit candidate = createKebun(null, "KB-0002", 500, 500, 200);
        when(repository.findAll()).thenReturn(List.of(existing));
        assertDoesNotThrow(() -> overlapChecker.rejectOverlap(candidate, null));
    }

    @Test
    void overlap_shouldThrow() {
        KebunSawit existing = createKebun("id-1", "KB-0001", 0, 0, 200);
        KebunSawit candidate = createKebun(null, "KB-0002", 100, 100, 200);
        when(repository.findAll()).thenReturn(List.of(existing));

        KebunValidationException ex = assertThrows(KebunValidationException.class,
                () -> overlapChecker.rejectOverlap(candidate, null));
        assertTrue(ex.getMessage().contains("overlap"));
    }

    @Test
    void selfOverlap_shouldBeIgnored() {
        KebunSawit existing = createKebun("id-1", "KB-0001", 0, 0, 200);
        KebunSawit candidate = createKebun(null, "KB-0001", 0, 0, 200);
        when(repository.findAll()).thenReturn(List.of(existing));
        assertDoesNotThrow(() -> overlapChecker.rejectOverlap(candidate, "id-1"));
    }

    @Test
    void selfOverlap_nullIgnoredId_shouldThrow() {
        KebunSawit existing = createKebun("id-1", "KB-0001", 0, 0, 200);
        KebunSawit candidate = createKebun(null, "KB-0002", 0, 0, 200);
        when(repository.findAll()).thenReturn(List.of(existing));
        assertThrows(KebunValidationException.class,
                () -> overlapChecker.rejectOverlap(candidate, null));
    }
}
