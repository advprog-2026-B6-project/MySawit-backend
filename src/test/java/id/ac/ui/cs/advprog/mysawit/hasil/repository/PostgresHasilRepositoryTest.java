package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;

@ExtendWith(MockitoExtension.class)
class PostgresHasilRepositoryTest {

    @Mock
    private HasilJpaRepository jpaRepository;

    private PostgresHasilRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostgresHasilRepository(jpaRepository);
    }

    @Test
    void savePersistsAndReturnsDomainModel() {
        Hasil report = Hasil.of(
                "h-1",
                "buruh-1",
                LocalDate.of(2026, 3, 6),
                110.5,
                "Panen pagi",
                List.of("foto-1.jpg"),
                true,
                HasilStatus.SUBMITTED
        );
        given(jpaRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        Hasil saved = repository.save(report);

        assertEquals("h-1", saved.getId());
        assertEquals("buruh-1", saved.getWorkerId());
        assertEquals(HasilStatus.SUBMITTED, saved.getStatus());
        verify(jpaRepository).save(any());
    }

    @Test
    void findAllMapsEntitiesToDomain() {
        HasilEntity entity = HasilEntity.from(Hasil.of(
                "h-2",
                "buruh-2",
                LocalDate.of(2026, 3, 7),
                95.0,
                "Panen siang",
                List.of("foto-2.jpg"),
                true,
                HasilStatus.VERIFIED
        ));
        given(jpaRepository.findAll()).willReturn(List.of(entity));

        List<Hasil> reports = repository.findAll();

        assertEquals(1, reports.size());
        assertEquals("h-2", reports.get(0).getId());
        assertEquals(HasilStatus.VERIFIED, reports.get(0).getStatus());
    }

    @Test
    void findByWorkerIdAndDateMapsOptionalEntityToDomain() {
        HasilEntity entity = HasilEntity.from(Hasil.of(
                "h-3",
                "buruh-3",
                LocalDate.of(2026, 3, 8),
                88.0,
                "Panen sore",
                List.of("foto-3.jpg"),
                true,
                HasilStatus.REJECTED
        ));
        given(jpaRepository.findByWorkerIdAndHasilDate("buruh-3", LocalDate.of(2026, 3, 8)))
                .willReturn(Optional.of(entity));

        Optional<Hasil> report = repository.findByWorkerIdAndDate("buruh-3", LocalDate.of(2026, 3, 8));

        assertTrue(report.isPresent());
        assertEquals(HasilStatus.REJECTED, report.get().getStatus());
    }

    @Test
    void existsByWorkerIdAndDateDelegatesToJpaRepository() {
        given(jpaRepository.existsByWorkerIdAndHasilDate("buruh-4", LocalDate.of(2026, 3, 9)))
                .willReturn(true);

        assertTrue(repository.existsByWorkerIdAndDate("buruh-4", LocalDate.of(2026, 3, 9)));
    }
}
