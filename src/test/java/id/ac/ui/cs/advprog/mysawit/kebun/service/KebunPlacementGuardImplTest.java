package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KebunPlacementGuardImplTest {

    @Mock
    private KebunMandorJpaRepository kebunMandorRepository;

    @Mock
    private KebunSupirJpaRepository kebunSupirRepository;

    @InjectMocks
    private KebunPlacementGuardImpl guard;

    @Test
    void isMandorPlaced_assigned_returnsTrue() {
        when(kebunMandorRepository.existsByMandorId(10L)).thenReturn(true);
        assertTrue(guard.isMandorPlaced(10L));
    }

    @Test
    void isMandorPlaced_notAssigned_returnsFalse() {
        when(kebunMandorRepository.existsByMandorId(10L)).thenReturn(false);
        assertFalse(guard.isMandorPlaced(10L));
    }

    @Test
    void isSupirPlaced_assigned_returnsTrue() {
        when(kebunSupirRepository.existsBySupirId(20L)).thenReturn(true);
        assertTrue(guard.isSupirPlaced(20L));
    }

    @Test
    void isSupirPlaced_notAssigned_returnsFalse() {
        when(kebunSupirRepository.existsBySupirId(20L)).thenReturn(false);
        assertFalse(guard.isSupirPlaced(20L));
    }

    @Test
    void areInSameKebun_sameKebun_returnsTrue() {
        KebunMandorEntity mandorAssignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
        KebunSupirEntity supirAssignment = new KebunSupirEntity("sa-1", "kebun-1", 20L);

        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(mandorAssignment));
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(supirAssignment));

        assertTrue(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_differentKebun_returnsFalse() {
        KebunMandorEntity mandorAssignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
        KebunSupirEntity supirAssignment = new KebunSupirEntity("sa-1", "kebun-2", 20L);

        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(mandorAssignment));
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(supirAssignment));

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_mandorNotPlaced_returnsFalse() {
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.empty());

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_supirNotPlaced_returnsFalse() {
        KebunMandorEntity mandorAssignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(mandorAssignment));
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.empty());

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void getKebunIdByMandorId_assigned_returnsKebunId() {
        KebunMandorEntity assignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(assignment));

        Optional<String> result = guard.getKebunIdByMandorId(10L);
        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void getKebunIdByMandorId_notAssigned_returnsEmpty() {
        when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.empty());

        Optional<String> result = guard.getKebunIdByMandorId(10L);
        assertTrue(result.isEmpty());
    }

    @Test
    void getKebunIdBySupirId_assigned_returnsKebunId() {
        KebunSupirEntity assignment = new KebunSupirEntity("sa-1", "kebun-1", 20L);
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(assignment));

        Optional<String> result = guard.getKebunIdBySupirId(20L);
        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void getKebunIdBySupirId_notAssigned_returnsEmpty() {
        when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.empty());

        Optional<String> result = guard.getKebunIdBySupirId(20L);
        assertTrue(result.isEmpty());
    }
}
