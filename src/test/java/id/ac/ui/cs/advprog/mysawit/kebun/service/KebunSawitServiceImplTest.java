package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunResponseMapper;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KebunSawitServiceImplTest {

    @Mock
    private KebunSawitRepository repository;

    @Mock
    private KebunAssignmentRepository assignmentRepository;

    @Mock
    private KebunUserReader userReader;

    private KebunSawitServiceImpl service;

    @BeforeEach
    void setUp() {
        KebunGeometry geometry = new KebunGeometry();
        KebunOverlapChecker overlapChecker = new KebunOverlapChecker(repository);
        KebunValidator validator = new KebunValidator(repository, geometry, overlapChecker);
        KebunDetailAssembler detailAssembler = new KebunDetailAssembler(
                repository,
                assignmentRepository,
                userReader,
                new KebunResponseMapper());
        service = new KebunSawitServiceImpl(
                repository,
                assignmentRepository,
                geometry,
                validator,
                detailAssembler);
    }

    private KebunSawit createValidKebun(String id, String kode, double x, double y, double size) {
        KebunSawit kebun = new KebunSawit();
        kebun.setId(id);
        kebun.setNamaKebun("Kebun " + kode);
        kebun.setKodeUnik(kode);
        kebun.setKiriAtas(new Coordinate(x, y + size));
        kebun.setKiriBawah(new Coordinate(x, y));
        kebun.setKananAtas(new Coordinate(x + size, y + size));
        kebun.setKananBawah(new Coordinate(x + size, y));
        kebun.setLuasHektare(size * size / 10000.0);
        return kebun;
    }

    private KebunSawit createRhombusKebun(String id, String kode) {
        KebunSawit kebun = new KebunSawit();
        kebun.setId(id);
        kebun.setNamaKebun("Kebun " + kode);
        kebun.setKodeUnik(kode);
        kebun.setKiriAtas(new Coordinate(0, 80));
        kebun.setKiriBawah(new Coordinate(60, 0));
        kebun.setKananAtas(new Coordinate(100, 80));
        kebun.setKananBawah(new Coordinate(160, 0));
        return kebun;
    }

    @Nested
    class CreateTests {
        @Test
        void create_validKebun_shouldSucceed() {
            KebunSawit kebun = createValidKebun(null, "KB-0001", 0, 0, 200);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());
            when(repository.findAll()).thenReturn(List.of());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.create(kebun);

            assertNotNull(result.getId());
            assertEquals("KB-0001", result.getKodeUnik());
            assertEquals(4.0, result.getLuasHektare(), 0.001);
        }

        @Test
        void create_duplicateKode_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001", 0, 0, 200);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.of(kebun));

            assertThrows(IllegalArgumentException.class, () -> service.create(kebun));
        }

        @Test
        void create_invalidKodeFormat_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "INVALID", 0, 0, 200);

            assertThrows(IllegalArgumentException.class, () -> service.create(kebun));
        }

        @Test
        void create_nullNama_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001", 0, 0, 200);
            kebun.setNamaKebun(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> service.create(kebun));
        }

        @Test
        void create_nullCoordinate_shouldThrow() {
            KebunSawit kebun = createValidKebun(null, "KB-0001", 0, 0, 200);
            kebun.setKiriAtas(null);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> service.create(kebun));
        }

        @Test
        void create_overlappingKebun_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit newKebun = createValidKebun(null, "KB-0002", 100, 100, 200);
            when(repository.findByKodeUnik("KB-0002")).thenReturn(Optional.empty());
            when(repository.findAll()).thenReturn(List.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.create(newKebun));
        }

        @Test
        void create_rhombusCoordinates_shouldThrow() {
            KebunSawit kebun = createRhombusKebun(null, "KB-0001");
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> service.create(kebun));
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void update_validData_shouldSucceed() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-9999", 0, 0, 300);
            updated.setNamaKebun("Nama Baru");

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.update("id-1", updated);

            assertEquals("Nama Baru", result.getNamaKebun());
            assertEquals("KB-0001", result.getKodeUnik());
            assertEquals(9.0, result.getLuasHektare(), 0.001);
        }

        @Test
        void update_nonExistentKebun_shouldThrow() {
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            when(repository.findById("missing")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> service.update("missing", updated));

            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }

        @Test
        void update_invalidCoordinates_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            updated.setKiriAtas(null);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_overlapWithOther_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit other = createValidKebun("id-2", "KB-0002", 500, 500, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 500, 500, 200);

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing, other));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_nullNama_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            updated.setNamaKebun(null);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_noMandor_shouldSucceed() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(assignmentRepository.kebunHasMandor("id-1")).thenReturn(false);

            assertDoesNotThrow(() -> service.delete("id-1"));

            verify(repository).deleteById("id-1");
        }

        @Test
        void delete_withMandorAssigned_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(assignmentRepository.kebunHasMandor("id-1")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> service.delete("id-1"));

            assertTrue(exception.getMessage().contains("masih memiliki Mandor"));
            verify(repository, never()).deleteById(any());
        }

        @Test
        void delete_nonExistent_shouldThrow() {
            when(repository.findById("missing")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> service.delete("missing"));

            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_noFilter_delegatesToRepositorySearch() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit k2 = createValidKebun("id-2", "KB-0002", 500, 0, 200);
            when(repository.search("", "")).thenReturn(List.of(k1, k2));

            List<KebunSawit> result = service.findAll("", "");

            assertEquals(2, result.size());
            verify(repository).search("", "");
            verify(repository, never()).findAll();
        }

        @Test
        void findAll_filters_delegatesToRepositorySearch() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.search("Utara", "0001")).thenReturn(List.of(kebun));

            List<KebunSawit> result = service.findAll("Utara", "0001");

            assertEquals(1, result.size());
            assertEquals("KB-0001", result.get(0).getKodeUnik());
            verify(repository).search("Utara", "0001");
            verify(repository, never()).findAll();
        }

        @Test
        void findByKodeUnik_delegatesToRepository() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.of(kebun));

            Optional<KebunSawit> result = service.findByKodeUnik("KB-0001");

            assertTrue(result.isPresent());
            assertEquals("id-1", result.get().getId());
            verify(repository).findByKodeUnik("KB-0001");
        }
    }

    @Nested
    class GetDetailTests {
        @Test
        void getDetail_withMandorAndSupirs_shouldReturnFullDetail() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.of(10L));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Pak Mandor", "mandor1", Role.MANDOR, "CERT-001")));
            when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L, 21L));
            when(userReader.findUsersByIds(List.of(20L, 21L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Supir Andi", "supir1", Role.SUPIR, null),
                    new UserSnapshot(21L, "Supir Budi", "supir2", Role.SUPIR, null)));

            KebunDetailResponse detail = service.getDetail("id-1", null);

            assertEquals("KB-0001", detail.getKodeUnik());
            assertNotNull(detail.getMandor());
            assertEquals("Pak Mandor", detail.getMandor().getFullname());
            assertEquals("CERT-001", detail.getMandor().getCertificationNumber());
            assertEquals(2, detail.getSupirList().size());
        }

        @Test
        void getDetail_noMandorOrSupir_shouldReturnEmptyDetailAssignments() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
            when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of());

            KebunDetailResponse detail = service.getDetail("id-1", null);

            assertNull(detail.getMandor());
            assertTrue(detail.getSupirList().isEmpty());
        }

        @Test
        void getDetail_filterSupirByName_shouldReturnFiltered() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());
            when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L, 21L));
            when(userReader.findUsersByIds(List.of(20L, 21L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Andi Supir", "supir1", Role.SUPIR, null),
                    new UserSnapshot(21L, "Budi Driver", "supir2", Role.SUPIR, null)));

            KebunDetailResponse detail = service.getDetail("id-1", "Andi");

            assertEquals(1, detail.getSupirList().size());
            assertEquals("Andi Supir", detail.getSupirList().get(0).getFullname());
        }

        @Test
        void getDetail_nonExistentKebun_shouldThrow() {
            when(repository.findById("missing")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> service.getDetail("missing", null));
        }
    }
}
