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

    // CREATE TESTS
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

        @Test
        void create_validAxisAlignedSquare_shouldSucceed() {
            KebunSawit kebun = createValidKebun(null, "KB-0001", -100, -100, 200);
            when(repository.findByKodeUnik("KB-0001")).thenReturn(Optional.empty());
            when(repository.findAll()).thenReturn(List.of());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.create(kebun);

            assertNotNull(result.getId());
            assertEquals(4.0, result.getLuasHektare(), 0.001);
        }
    }

    // UPDATE TESTS
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
            // KodeUnik should be locked to original
            assertEquals("KB-0001", result.getKodeUnik());
        }

        @Test
        void update_preservesKodeUnik() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-CHANGED", 0, 0, 200);

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.update("id-1", updated);
            assertEquals("KB-0001", result.getKodeUnik());
        }

        @Test
        void update_recalculatesLuas() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 300);

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.update("id-1", updated);
            // 300*300 = 90000 m2 = 9.0 hectare
            assertEquals(90000.0 / 10000.0, result.getLuasHektare(), 0.001);
        }

        @Test
        void update_nonExistentKebun_shouldThrow() {
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            when(repository.findById("nonexistent")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update("nonexistent", updated));
            assertTrue(ex.getMessage().contains("tidak ditemukan"));
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
        void update_notSquare_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = new KebunSawit();
            updated.setNamaKebun("Test");
            updated.setKiriAtas(new Coordinate(0, 100));
            updated.setKiriBawah(new Coordinate(0, 0));
            updated.setKananAtas(new Coordinate(200, 100)); // Not a square
            updated.setKananBawah(new Coordinate(100, 0));

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_overlapWithOther_shouldThrow() {
            KebunSawit existing1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit existing2 = createValidKebun("id-2", "KB-0002", 500, 500, 200);

            // Try to move kebun1 to overlap with kebun2
            KebunSawit updated = createValidKebun(null, "KB-0001", 500, 500, 200);

            when(repository.findById("id-1")).thenReturn(Optional.of(existing1));
            when(repository.findAll()).thenReturn(List.of(existing1, existing2));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_samePositionDoesNotSelfOverlap_shouldSucceed() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            updated.setNamaKebun("Nama Baru");

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.update("id-1", updated);
            assertEquals("Nama Baru", result.getNamaKebun());
        }

        @Test
        void update_nullNama_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", 0, 0, 200);
            updated.setNamaKebun(null);

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_rhombusCoordinates_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createRhombusKebun(null, "KB-0001");

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));

            assertThrows(IllegalArgumentException.class, () -> service.update("id-1", updated));
        }

        @Test
        void update_validAxisAlignedSquare_shouldSucceed() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit updated = createValidKebun(null, "KB-0001", -100, -100, 200);
            updated.setNamaKebun("Kebun Repositioned");

            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            KebunSawit result = service.update("id-1", updated);

            assertEquals("Kebun Repositioned", result.getNamaKebun());
            assertEquals(4.0, result.getLuasHektare(), 0.001);
            assertEquals(-100, result.getKiriBawah().getX(), 0.001);
        }
    }

    // DELETE TESTS
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

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.delete("id-1"));
            assertTrue(ex.getMessage().contains("masih memiliki Mandor"));
            verify(repository, never()).deleteById(any());
        }

        @Test
        void delete_withSupirButNoMandor_currentlySucceeds() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(assignmentRepository.kebunHasMandor("id-1")).thenReturn(false);

            assertDoesNotThrow(() -> service.delete("id-1"));

            verify(repository).deleteById("id-1");
            verify(assignmentRepository, never()).findSupirIdsByKebunId(anyString());
        }

        @Test
        void delete_nonExistent_shouldThrow() {
            when(repository.findById("nonexistent")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.delete("nonexistent"));
            assertTrue(ex.getMessage().contains("tidak ditemukan"));
        }
    }

    // FIND ALL TESTS
    @Nested
    class FindAllTests {
        @Test
        void findAll_noFilter_returnsAll() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit k2 = createValidKebun("id-2", "KB-0002", 500, 0, 200);
            when(repository.search("", "")).thenReturn(List.of(k1, k2));

            List<KebunSawit> result = service.findAll("", "");
            assertEquals(2, result.size());
        }

        @Test
        void findAll_filterByNama_returnsFiltered() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            k1.setNamaKebun("Kebun Utara");
            KebunSawit k2 = createValidKebun("id-2", "KB-0002", 500, 0, 200);
            k2.setNamaKebun("Kebun Selatan");
            when(repository.search("Utara", "")).thenReturn(List.of(k1));

            List<KebunSawit> result = service.findAll("Utara", "");
            assertEquals(1, result.size());
            assertEquals("Kebun Utara", result.get(0).getNamaKebun());
        }

        @Test
        void findAll_filterByKode_returnsFiltered() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.search("", "0001")).thenReturn(List.of(k1));

            List<KebunSawit> result = service.findAll("", "0001");
            assertEquals(1, result.size());
            assertEquals("KB-0001", result.get(0).getKodeUnik());
        }
    }

    // GET DETAIL TESTS
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
        void getDetail_noMandor_shouldReturnNullMandor() {
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
        void getDetail_filterSupirByName_handlesNullFullname() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());

            when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L));
            when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                    new UserSnapshot(20L, null, "supir1", Role.SUPIR, null)));

            KebunDetailResponse detail = service.getDetail("id-1", "Andi");
            assertTrue(detail.getSupirList().isEmpty());
        }

        @Test
        void getDetail_filterSupirByEmptyString_returnsAll() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(assignmentRepository.findMandorIdByKebunId("id-1")).thenReturn(Optional.empty());

            when(assignmentRepository.findSupirIdsByKebunId("id-1")).thenReturn(List.of(20L));
            when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Andi Supir", "supir1", Role.SUPIR, null)));

            KebunDetailResponse detail = service.getDetail("id-1", "");
            assertEquals(1, detail.getSupirList().size());
        }

        @Test
        void findAll_nullFilters_returnsAll() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.search(null, null)).thenReturn(List.of(k1));
            List<KebunSawit> result = service.findAll(null, null);
            assertEquals(1, result.size());
        }

        @Test
        void getDetail_nonExistentKebun_shouldThrow() {
            when(repository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.getDetail("nonexistent", null));
        }
    }
}
