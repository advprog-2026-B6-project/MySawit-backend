package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryKebunSawitRepositoryTest {

    private InMemoryKebunSawitRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKebunSawitRepository();
        repository.save(createKebun("id-1", "Kebun Utara", "KB-0001"));
        repository.save(createKebun("id-2", "Kebun Selatan", "KB-0002"));
        repository.save(createKebun("id-3", "Kebun Perf", "PF-0001"));
    }

    @Test
    void search_withNullFilters_returnsAllKebun() {
        List<KebunSawit> result = repository.search(null, null);

        assertEquals(3, result.size());
    }

    @Test
    void search_filtersByNamaCaseInsensitively() {
        List<KebunSawit> result = repository.search("utara", "");

        assertEquals(1, result.size());
        assertEquals("KB-0001", result.get(0).getKodeUnik());
    }

    @Test
    void search_filtersByKodeCaseInsensitively() {
        List<KebunSawit> result = repository.search("", "pf-0001");

        assertEquals(1, result.size());
        assertEquals("Kebun Perf", result.get(0).getNamaKebun());
    }

    @Test
    void search_filtersByNamaAndKode() {
        List<KebunSawit> result = repository.search("kebun", "0002");

        assertEquals(1, result.size());
        assertEquals("id-2", result.get(0).getId());
    }

    private KebunSawit createKebun(String id, String nama, String kode) {
        return new KebunSawit(
                id,
                nama,
                kode,
                4.0,
                new Coordinate(0, 200),
                new Coordinate(0, 0),
                new Coordinate(200, 200),
                new Coordinate(200, 0));
    }
}
