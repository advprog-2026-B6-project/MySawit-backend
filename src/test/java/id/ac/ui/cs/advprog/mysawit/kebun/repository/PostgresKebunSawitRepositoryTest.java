package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostgresKebunSawitRepositoryTest {

    @Mock
    private KebunSawitJpaRepository jpaRepository;

    private PostgresKebunSawitRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostgresKebunSawitRepository(jpaRepository, new KebunSawitEntityMapper());
    }

    private KebunSawit createDomain(String id, String kode) {
        return new KebunSawit(
                id,
                "Kebun " + kode,
                kode,
                4.0,
                new Coordinate(0, 200),
                new Coordinate(0, 0),
                new Coordinate(200, 200),
                new Coordinate(200, 0));
    }

    private KebunSawitEntity createEntity(String id, String kode) {
        return new KebunSawitEntity(
                id,
                "Kebun " + kode,
                kode,
                4.0,
                new CoordinateEmbeddable(0.0, 200.0),
                new CoordinateEmbeddable(0.0, 0.0),
                new CoordinateEmbeddable(200.0, 200.0),
                new CoordinateEmbeddable(200.0, 0.0));
    }

    @Test
    void save_mapsDomainToEntityAndReturnsSavedDomain() {
        KebunSawit kebun = createDomain("id-1", "KB-0001");
        when(jpaRepository.save(any(KebunSawitEntity.class)))
                .thenReturn(createEntity("id-1", "KB-0001"));

        KebunSawit result = repository.save(kebun);

        ArgumentCaptor<KebunSawitEntity> captor = ArgumentCaptor.forClass(KebunSawitEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals("id-1", captor.getValue().getId());
        assertEquals("KB-0001", captor.getValue().getKodeUnik());
        assertEquals("id-1", result.getId());
        assertEquals("KB-0001", result.getKodeUnik());
    }

    @Test
    void findByKodeUnik_foundMapsEntityToDomain() {
        when(jpaRepository.findByKodeUnik("KB-0001"))
                .thenReturn(Optional.of(createEntity("id-1", "KB-0001")));

        Optional<KebunSawit> result = repository.findByKodeUnik("KB-0001");

        assertTrue(result.isPresent());
        assertEquals("id-1", result.get().getId());
        assertEquals("KB-0001", result.get().getKodeUnik());
    }

    @Test
    void findByKodeUnik_notFoundReturnsEmpty() {
        when(jpaRepository.findByKodeUnik("KB-404")).thenReturn(Optional.empty());

        Optional<KebunSawit> result = repository.findByKodeUnik("KB-404");

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_foundMapsEntityToDomain() {
        when(jpaRepository.findById("id-1")).thenReturn(Optional.of(createEntity("id-1", "KB-0001")));

        Optional<KebunSawit> result = repository.findById("id-1");

        assertTrue(result.isPresent());
        assertEquals("KB-0001", result.get().getKodeUnik());
    }

    @Test
    void findById_notFoundReturnsEmpty() {
        when(jpaRepository.findById("missing")).thenReturn(Optional.empty());

        Optional<KebunSawit> result = repository.findById("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_mapsEveryEntity() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                createEntity("id-1", "KB-0001"),
                createEntity("id-2", "KB-0002")));

        List<KebunSawit> result = repository.findAll();

        assertEquals(2, result.size());
        assertEquals(List.of("KB-0001", "KB-0002"),
                result.stream().map(KebunSawit::getKodeUnik).toList());
    }

    @Test
    void search_withValuesPassesValuesThroughAndMapsResults() {
        when(jpaRepository.search("Utara", "0001"))
                .thenReturn(List.of(createEntity("id-1", "KB-0001")));

        List<KebunSawit> result = repository.search("Utara", "0001");

        verify(jpaRepository).search("Utara", "0001");
        assertEquals(1, result.size());
        assertEquals("KB-0001", result.get(0).getKodeUnik());
    }

    @Test
    void search_withNullsNormalizesToEmptyStrings() {
        when(jpaRepository.search("", "")).thenReturn(List.of(createEntity("id-1", "KB-0001")));

        List<KebunSawit> result = repository.search(null, null);

        verify(jpaRepository).search("", "");
        assertEquals(1, result.size());
    }

    @Test
    void deleteById_delegatesToJpaRepository() {
        repository.deleteById("id-1");

        verify(jpaRepository).deleteById("id-1");
    }
}
