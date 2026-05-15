package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
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
    private KebunAssignmentRepository assignmentRepository;

    @InjectMocks
    private KebunPlacementGuardImpl guard;

    @Test
    void isMandorPlaced_assigned_returnsTrue() {
        when(assignmentRepository.mandorIsAssigned(10L)).thenReturn(true);
        assertTrue(guard.isMandorPlaced(10L));
    }

    @Test
    void isMandorPlaced_notAssigned_returnsFalse() {
        when(assignmentRepository.mandorIsAssigned(10L)).thenReturn(false);
        assertFalse(guard.isMandorPlaced(10L));
    }

    @Test
    void isSupirPlaced_assigned_returnsTrue() {
        when(assignmentRepository.supirIsAssigned(20L)).thenReturn(true);
        assertTrue(guard.isSupirPlaced(20L));
    }

    @Test
    void isSupirPlaced_notAssigned_returnsFalse() {
        when(assignmentRepository.supirIsAssigned(20L)).thenReturn(false);
        assertFalse(guard.isSupirPlaced(20L));
    }

    @Test
    void areInSameKebun_sameKebun_returnsTrue() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.of("kebun-1"));
        when(assignmentRepository.findKebunIdBySupirId(20L)).thenReturn(Optional.of("kebun-1"));

        assertTrue(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_differentKebun_returnsFalse() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.of("kebun-1"));
        when(assignmentRepository.findKebunIdBySupirId(20L)).thenReturn(Optional.of("kebun-2"));

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_mandorNotPlaced_returnsFalse() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.empty());

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void areInSameKebun_supirNotPlaced_returnsFalse() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.of("kebun-1"));
        when(assignmentRepository.findKebunIdBySupirId(20L)).thenReturn(Optional.empty());

        assertFalse(guard.areInSameKebun(10L, 20L));
    }

    @Test
    void getKebunIdByMandorId_assigned_returnsKebunId() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.of("kebun-1"));

        Optional<String> result = guard.getKebunIdByMandorId(10L);
        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void getKebunIdByMandorId_notAssigned_returnsEmpty() {
        when(assignmentRepository.findKebunIdByMandorId(10L)).thenReturn(Optional.empty());

        Optional<String> result = guard.getKebunIdByMandorId(10L);
        assertTrue(result.isEmpty());
    }

    @Test
    void getKebunIdBySupirId_assigned_returnsKebunId() {
        when(assignmentRepository.findKebunIdBySupirId(20L)).thenReturn(Optional.of("kebun-1"));

        Optional<String> result = guard.getKebunIdBySupirId(20L);
        assertTrue(result.isPresent());
        assertEquals("kebun-1", result.get());
    }

    @Test
    void getKebunIdBySupirId_notAssigned_returnsEmpty() {
        when(assignmentRepository.findKebunIdBySupirId(20L)).thenReturn(Optional.empty());

        Optional<String> result = guard.getKebunIdBySupirId(20L);
        assertTrue(result.isEmpty());
    }
}
