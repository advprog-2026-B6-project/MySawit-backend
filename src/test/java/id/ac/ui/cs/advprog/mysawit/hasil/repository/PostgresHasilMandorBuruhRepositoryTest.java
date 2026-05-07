package id.ac.ui.cs.advprog.mysawit.hasil.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostgresHasilMandorBuruhRepositoryTest {

    @Mock
    private HasilMandorBuruhJpaRepository jpaRepository;

    private PostgresHasilMandorBuruhRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PostgresHasilMandorBuruhRepository(jpaRepository);
    }

    @Test
    void savePersistsJoinRow() {
        HasilMandorBuruhEntity assignment = new HasilMandorBuruhEntity(null, 10L, 1L);
        given(jpaRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        HasilMandorBuruhEntity saved = repository.save(assignment);

        assertEquals(10L, saved.getMandorId());
        assertEquals(1L, saved.getBuruhId());
        verify(jpaRepository).save(any());
    }

    @Test
    void findBuruhIdsByMandorIdMapsEntities() {
        HasilMandorBuruhEntity assignment1 = new HasilMandorBuruhEntity("1", 10L, 1L);
        HasilMandorBuruhEntity assignment2 = new HasilMandorBuruhEntity("2", 10L, 2L);
        given(jpaRepository.findAllByMandorId(10L)).willReturn(List.of(assignment1, assignment2));

        List<Long> buruhIds = repository.findBuruhIdsByMandorId(10L);

        assertEquals(List.of(1L, 2L), buruhIds);
    }

    @Test
    void existsByMandorIdAndBuruhIdDelegatesToJpaRepository() {
        given(jpaRepository.existsByMandorIdAndBuruhId(10L, 1L)).willReturn(true);

        assertTrue(repository.existsByMandorIdAndBuruhId(10L, 1L));
    }
}
