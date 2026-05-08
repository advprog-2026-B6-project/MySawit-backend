package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunAssignmentServiceImplTest {

    @Mock
    private KebunSawitRepository kebunRepository;

    @Mock
    private KebunMandorJpaRepository kebunMandorRepository;

    @Mock
    private KebunSupirJpaRepository kebunSupirRepository;

    @Mock
    private KebunUserReader userReader;

    @InjectMocks
    private KebunAssignmentServiceImpl service;

    private KebunSawit createKebun(String id) {
        KebunSawit kebun = new KebunSawit();
        kebun.setId(id);
        kebun.setNamaKebun("Kebun " + id);
        kebun.setKodeUnik("KB-0001");
        kebun.setKiriAtas(new Coordinate(0, 200));
        kebun.setKiriBawah(new Coordinate(0, 0));
        kebun.setKananAtas(new Coordinate(200, 200));
        kebun.setKananBawah(new Coordinate(200, 0));
        return kebun;
    }

    // =====================================================================
    // MANDOR ASSIGNMENT TESTS
    // =====================================================================
    @Nested
    class AssignMandorTests {
        @Test
        void assignMandor_validInput_shouldSucceed() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Mandor A", "mandor_a", "MANDOR", "CERT-001")));
            when(kebunMandorRepository.existsByKebunId("kebun-1")).thenReturn(false);
            when(kebunMandorRepository.existsByMandorId(10L)).thenReturn(false);

            assertDoesNotThrow(() -> service.assignMandor("kebun-1", 10L));
            verify(kebunMandorRepository).save(any(KebunMandorEntity.class));
        }

        @Test
        void assignMandor_kebunNotFound_shouldThrow() {
            when(kebunRepository.findById("nonexistent")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.assignMandor("nonexistent", 10L));
            assertTrue(ex.getMessage().contains("Kebun tidak ditemukan"));
        }

        @Test
        void assignMandor_userNotFound_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(10L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.assignMandor("kebun-1", 10L));
            assertTrue(ex.getMessage().contains("User tidak ditemukan"));
        }

        @Test
        void assignMandor_wrongRole_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Buruh A", "buruh_a", "BURUH", null)));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.assignMandor("kebun-1", 10L));
            assertTrue(ex.getMessage().contains("bukan Mandor"));
        }

        @Test
        void assignMandor_kebunAlreadyHasMandor_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Mandor A", "mandor_a", "MANDOR", "CERT-001")));
            when(kebunMandorRepository.existsByKebunId("kebun-1")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.assignMandor("kebun-1", 10L));
            assertTrue(ex.getMessage().contains("sudah memiliki Mandor"));
        }

        @Test
        void assignMandor_mandorAlreadyAssigned_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Mandor A", "mandor_a", "MANDOR", "CERT-001")));
            when(kebunMandorRepository.existsByKebunId("kebun-1")).thenReturn(false);
            when(kebunMandorRepository.existsByMandorId(10L)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.assignMandor("kebun-1", 10L));
            assertTrue(ex.getMessage().contains("sudah ditugaskan"));
        }
    }

    // =====================================================================
    // MANDOR REASSIGNMENT TESTS
    // =====================================================================
    @Nested
    class ReassignMandorTests {
        @Test
        void reassignMandor_validInput_shouldSucceed() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));

            KebunMandorEntity currentAssignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
            when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(currentAssignment));
            when(kebunMandorRepository.existsByKebunId("kebun-2")).thenReturn(false);

            assertDoesNotThrow(() -> service.reassignMandor(10L, "kebun-1", "kebun-2"));
            verify(kebunMandorRepository).delete(currentAssignment);
            verify(kebunMandorRepository).save(any(KebunMandorEntity.class));
        }

        @Test
        void reassignMandor_fromKebunNotFound_shouldThrow() {
            when(kebunRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignMandor(10L, "nonexistent", "kebun-2"));
        }

        @Test
        void reassignMandor_toKebunNotFound_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignMandor(10L, "kebun-1", "nonexistent"));
        }

        @Test
        void reassignMandor_mandorNotAtFromKebun_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));

            KebunMandorEntity currentAssignment = new KebunMandorEntity("ma-1", "kebun-3", 10L);
            when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(currentAssignment));

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignMandor(10L, "kebun-1", "kebun-2"));
        }

        @Test
        void reassignMandor_toKebunAlreadyHasMandor_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));

            KebunMandorEntity currentAssignment = new KebunMandorEntity("ma-1", "kebun-1", 10L);
            when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.of(currentAssignment));
            when(kebunMandorRepository.existsByKebunId("kebun-2")).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignMandor(10L, "kebun-1", "kebun-2"));
        }

        @Test
        void reassignMandor_mandorNotAssignedAnywhere_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));
            when(kebunMandorRepository.findByMandorId(10L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignMandor(10L, "kebun-1", "kebun-2"));
        }
    }

    // =====================================================================
    // SUPIR ASSIGNMENT TESTS
    // =====================================================================
    @Nested
    class AssignSupirTests {
        @Test
        void assignSupir_validInput_shouldSucceed() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(20L)).thenReturn(Optional.of(
                    new UserSnapshot(20L, "Supir A", "supir_a", "SUPIR", null)));
            when(kebunSupirRepository.existsBySupirId(20L)).thenReturn(false);

            assertDoesNotThrow(() -> service.assignSupir("kebun-1", 20L));
            verify(kebunSupirRepository).save(any(KebunSupirEntity.class));
        }

        @Test
        void assignSupir_kebunNotFound_shouldThrow() {
            when(kebunRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.assignSupir("nonexistent", 20L));
        }

        @Test
        void assignSupir_userNotFound_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(20L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.assignSupir("kebun-1", 20L));
        }

        @Test
        void assignSupir_wrongRole_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(20L)).thenReturn(Optional.of(
                    new UserSnapshot(20L, "Mandor X", "mandor_x", "MANDOR", "CERT-001")));

            assertThrows(IllegalArgumentException.class,
                    () -> service.assignSupir("kebun-1", 20L));
        }

        @Test
        void assignSupir_alreadyAssigned_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(20L)).thenReturn(Optional.of(
                    new UserSnapshot(20L, "Supir A", "supir_a", "SUPIR", null)));
            when(kebunSupirRepository.existsBySupirId(20L)).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                    () -> service.assignSupir("kebun-1", 20L));
        }

        @Test
        void assignSupir_secondSupirToSameKebun_shouldSucceed() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(userReader.findUserById(21L)).thenReturn(Optional.of(
                    new UserSnapshot(21L, "Supir B", "supir_b", "SUPIR", null)));
            when(kebunSupirRepository.existsBySupirId(21L)).thenReturn(false);

            assertDoesNotThrow(() -> service.assignSupir("kebun-1", 21L));
            verify(kebunSupirRepository).save(any(KebunSupirEntity.class));
        }
    }

    // =====================================================================
    // SUPIR REASSIGNMENT TESTS
    // =====================================================================
    @Nested
    class ReassignSupirTests {
        @Test
        void reassignSupir_validInput_shouldSucceed() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));

            KebunSupirEntity currentAssignment = new KebunSupirEntity("sa-1", "kebun-1", 20L);
            when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(currentAssignment));

            assertDoesNotThrow(() -> service.reassignSupir(20L, "kebun-1", "kebun-2"));
            verify(kebunSupirRepository).delete(currentAssignment);
            verify(kebunSupirRepository).save(any(KebunSupirEntity.class));
        }

        @Test
        void reassignSupir_notAtFromKebun_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));

            KebunSupirEntity currentAssignment = new KebunSupirEntity("sa-1", "kebun-3", 20L);
            when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.of(currentAssignment));

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignSupir(20L, "kebun-1", "kebun-2"));
        }

        @Test
        void reassignSupir_notAssignedAnywhere_shouldThrow() {
            when(kebunRepository.findById("kebun-1")).thenReturn(Optional.of(createKebun("kebun-1")));
            when(kebunRepository.findById("kebun-2")).thenReturn(Optional.of(createKebun("kebun-2")));
            when(kebunSupirRepository.findBySupirId(20L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.reassignSupir(20L, "kebun-1", "kebun-2"));
        }
    }
}
