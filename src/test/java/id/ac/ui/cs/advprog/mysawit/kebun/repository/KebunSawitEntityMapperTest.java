package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KebunSawitEntityMapperTest {

    private KebunSawitEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new KebunSawitEntityMapper();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        KebunSawit domain = new KebunSawit(
                "id-1", "Kebun Test", "KB-0001", 4.0,
                new Coordinate(0, 200), new Coordinate(0, 0),
                new Coordinate(200, 200), new Coordinate(200, 0));

        KebunSawitEntity entity = mapper.toEntity(domain);

        assertEquals("id-1", entity.getId());
        assertEquals("Kebun Test", entity.getNamaKebun());
        assertEquals("KB-0001", entity.getKodeUnik());
        assertEquals(4.0, entity.getLuasHektare());
        assertEquals(0, entity.getKiriAtas().getX());
        assertEquals(200, entity.getKiriAtas().getY());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        KebunSawitEntity entity = new KebunSawitEntity(
                "id-1", "Kebun Test", "KB-0001", 4.0,
                new CoordinateEmbeddable(0.0, 200.0),
                new CoordinateEmbeddable(0.0, 0.0),
                new CoordinateEmbeddable(200.0, 200.0),
                new CoordinateEmbeddable(200.0, 0.0));

        KebunSawit domain = mapper.toDomain(entity);

        assertEquals("id-1", domain.getId());
        assertEquals("Kebun Test", domain.getNamaKebun());
        assertEquals("KB-0001", domain.getKodeUnik());
        assertEquals(4.0, domain.getLuasHektare());
        assertEquals(0, domain.getKiriAtas().getX());
        assertEquals(200, domain.getKiriAtas().getY());
    }

    @Test
    void toEntity_nullCoordinates_shouldMapNulls() {
        KebunSawit domain = new KebunSawit("id-1", "Test", "KB-0001", 4.0,
                null, null, null, null);

        KebunSawitEntity entity = mapper.toEntity(domain);

        assertNull(entity.getKiriAtas());
        assertNull(entity.getKiriBawah());
        assertNull(entity.getKananAtas());
        assertNull(entity.getKananBawah());
    }

    @Test
    void toDomain_nullCoordinates_shouldMapNulls() {
        KebunSawitEntity entity = new KebunSawitEntity("id-1", "Test", "KB-0001", 4.0,
                null, null, null, null);

        KebunSawit domain = mapper.toDomain(entity);

        assertNull(domain.getKiriAtas());
        assertNull(domain.getKiriBawah());
        assertNull(domain.getKananAtas());
        assertNull(domain.getKananBawah());
    }

    @Test
    void roundTrip_shouldPreserveData() {
        KebunSawit original = new KebunSawit(
                "id-1", "Kebun Roundtrip", "KB-9999", 16.0,
                new Coordinate(100, 500), new Coordinate(100, 100),
                new Coordinate(500, 500), new Coordinate(500, 100));

        KebunSawitEntity entity = mapper.toEntity(original);
        KebunSawit roundTripped = mapper.toDomain(entity);

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getNamaKebun(), roundTripped.getNamaKebun());
        assertEquals(original.getKodeUnik(), roundTripped.getKodeUnik());
        assertEquals(original.getLuasHektare(), roundTripped.getLuasHektare());
        assertEquals(original.getKiriAtas().getX(), roundTripped.getKiriAtas().getX());
        assertEquals(original.getKiriAtas().getY(), roundTripped.getKiriAtas().getY());
    }
}
