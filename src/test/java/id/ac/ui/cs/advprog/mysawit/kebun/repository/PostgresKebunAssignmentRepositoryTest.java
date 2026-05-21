package id.ac.ui.cs.advprog.mysawit.kebun.repository;

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
class PostgresKebunAssignmentRepositoryTest {

    @Mock
    private KebunMandorJpaRepository kebunMandorRepository;

    @Mock
    private KebunSupirJpaRepository kebunSupirRepository;

    private PostgresKebunAssignmentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostgresKebunAssignmentRepository(kebunMandorRepository, kebunSupirRepository);
    }

    @Test
    void findMandorIdByKebunId_foundReturnsMandorId() {
        when(kebunMandorRepository.findByKebunId("kebun-1"))
                .thenReturn(Optional.of(new KebunMandorEntity("assignment-1", "kebun-1", 10L)));

        Optional<Long> result = repository.findMandorIdByKebunId("kebun-1");

        assertTrue(result.isPresent());
        assertEquals(10L, result.get());
    }

    @Test
    void findMandorIdByKebunId_notFoundReturnsEmpty() {
        when(kebunMandorRepository.findByKebunId("missing")).thenReturn(Optional.empty());

        Optional<Long> result = repository.findMandorIdByKebunId("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void findKebunIdByMandorId_foundReturnsKebunId() {
        when(kebunMandorRepository.findByMandorId(10L))
                .thenReturn(Optional.of(new KebunMandorEntity("assignment-1", "kebun-1", 10L)));

        Optional<String> result = repository.findKebunIdByMandorId(10L);

        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void findKebunIdByMandorId_notFoundReturnsEmpty() {
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.empty());

        Optional<String> result = repository.findKebunIdByMandorId(10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void kebunHasMandor_delegatesToMandorRepository() {
        when(kebunMandorRepository.existsByKebunId("kebun-1")).thenReturn(true);

        assertTrue(repository.kebunHasMandor("kebun-1"));
    }

    @Test
    void mandorIsAssigned_delegatesToMandorRepository() {
        when(kebunMandorRepository.existsByMandorId(10L)).thenReturn(true);

        assertTrue(repository.mandorIsAssigned(10L));
    }

    @Test
    void assignMandor_savesAssignmentEntity() {
        repository.assignMandor("kebun-1", 10L);

        ArgumentCaptor<KebunMandorEntity> captor = ArgumentCaptor.forClass(KebunMandorEntity.class);
        verify(kebunMandorRepository).save(captor.capture());
        assertEquals("kebun-1", captor.getValue().getKebunId());
        assertEquals(10L, captor.getValue().getMandorId());
    }

    @Test
    void moveMandor_existingAssignmentDeletesOldAndSavesNew() {
        KebunMandorEntity current = new KebunMandorEntity("assignment-1", "from", 10L);
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(current));

        repository.moveMandor(10L, "from", "to");

        verify(kebunMandorRepository).delete(current);
        ArgumentCaptor<KebunMandorEntity> captor = ArgumentCaptor.forClass(KebunMandorEntity.class);
        verify(kebunMandorRepository).save(captor.capture());
        assertEquals("to", captor.getValue().getKebunId());
        assertEquals(10L, captor.getValue().getMandorId());
    }

    @Test
    void moveMandor_missingAssignmentThrows() {
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repository.moveMandor(10L, "from", "to"));

        assertEquals("Mandor assignment not found", exception.getMessage());
        verify(kebunMandorRepository, never()).delete(any());
        verify(kebunMandorRepository, never()).save(any());
    }

    @Test
    void moveMandor_sourceMismatchThrows() {
        KebunMandorEntity current = new KebunMandorEntity("assignment-1", "actual", 10L);
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(current));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repository.moveMandor(10L, "from", "to"));

        assertEquals("Mandor assignment source kebun mismatch", exception.getMessage());
        verify(kebunMandorRepository, never()).delete(any());
        verify(kebunMandorRepository, never()).save(any());
    }

    @Test
    void findSupirIdsByKebunId_mapsSupirEntitiesToIds() {
        when(kebunSupirRepository.findAllByKebunId("kebun-1")).thenReturn(List.of(
                new KebunSupirEntity("assignment-1", "kebun-1", 20L),
                new KebunSupirEntity("assignment-2", "kebun-1", 21L)));

        List<Long> result = repository.findSupirIdsByKebunId("kebun-1");

        assertEquals(List.of(20L, 21L), result);
    }

    @Test
    void findKebunIdBySupirId_foundReturnsKebunId() {
        when(kebunSupirRepository.findBySupirId(20L))
                .thenReturn(Optional.of(new KebunSupirEntity("assignment-1", "kebun-1", 20L)));

        Optional<String> result = repository.findKebunIdBySupirId(20L);

        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void findKebunIdBySupirId_notFoundReturnsEmpty() {
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.empty());

        Optional<String> result = repository.findKebunIdBySupirId(20L);

        assertTrue(result.isEmpty());
    }

    @Test
    void supirIsAssigned_delegatesToSupirRepository() {
        when(kebunSupirRepository.existsBySupirId(20L)).thenReturn(true);

        assertTrue(repository.supirIsAssigned(20L));
    }

    @Test
    void assignSupir_savesAssignmentEntity() {
        repository.assignSupir("kebun-1", 20L);

        ArgumentCaptor<KebunSupirEntity> captor = ArgumentCaptor.forClass(KebunSupirEntity.class);
        verify(kebunSupirRepository).save(captor.capture());
        assertEquals("kebun-1", captor.getValue().getKebunId());
        assertEquals(20L, captor.getValue().getSupirId());
    }

    @Test
    void moveSupir_existingAssignmentDeletesOldAndSavesNew() {
        KebunSupirEntity current = new KebunSupirEntity("assignment-1", "from", 20L);
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(current));

        repository.moveSupir(20L, "from", "to");

        verify(kebunSupirRepository).delete(current);
        ArgumentCaptor<KebunSupirEntity> captor = ArgumentCaptor.forClass(KebunSupirEntity.class);
        verify(kebunSupirRepository).save(captor.capture());
        assertEquals("to", captor.getValue().getKebunId());
        assertEquals(20L, captor.getValue().getSupirId());
    }

    @Test
    void moveSupir_missingAssignmentThrows() {
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repository.moveSupir(20L, "from", "to"));

        assertEquals("Supir assignment not found", exception.getMessage());
        verify(kebunSupirRepository, never()).delete(any());
        verify(kebunSupirRepository, never()).save(any());
    }

    @Test
    void moveSupir_sourceMismatchThrows() {
        KebunSupirEntity current = new KebunSupirEntity("assignment-1", "actual", 20L);
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(current));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repository.moveSupir(20L, "from", "to"));

        assertEquals("Supir assignment source kebun mismatch", exception.getMessage());
        verify(kebunSupirRepository, never()).delete(any());
        verify(kebunSupirRepository, never()).save(any());
    }
}
