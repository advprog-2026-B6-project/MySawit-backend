package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private KebunMandorJpaRepository kebunMandorRepository;

    @Mock
    private KebunSupirJpaRepository kebunSupirRepository;

    @Mock
    private KebunUserReader userReader;

    @InjectMocks
    private KebunSawitServiceImpl service;

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

    // CREATE TESTS (existing from 25% milestone, ensure no regression)
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
            // 300*300 = 90000 m² = 9.0 hectare
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
    }

    // DELETE TESTS
    @Nested
    class DeleteTests {
        @Test
        void delete_noMandor_shouldSucceed() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(kebunMandorRepository.existsByKebunId("id-1")).thenReturn(false);

            assertDoesNotThrow(() -> service.delete("id-1"));
            verify(repository).deleteById("id-1");
        }

        @Test
        void delete_withMandorAssigned_shouldThrow() {
            KebunSawit existing = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(existing));
            when(kebunMandorRepository.existsByKebunId("id-1")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.delete("id-1"));
            assertTrue(ex.getMessage().contains("masih memiliki Mandor"));
            verify(repository, never()).deleteById(any());
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
            when(repository.findAll()).thenReturn(List.of(k1, k2));

            List<KebunSawit> result = service.findAll("", "");
            assertEquals(2, result.size());
        }

        @Test
        void findAll_filterByNama_returnsFiltered() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            k1.setNamaKebun("Kebun Utara");
            KebunSawit k2 = createValidKebun("id-2", "KB-0002", 500, 0, 200);
            k2.setNamaKebun("Kebun Selatan");
            when(repository.findAll()).thenReturn(List.of(k1, k2));

            List<KebunSawit> result = service.findAll("Utara", "");
            assertEquals(1, result.size());
            assertEquals("Kebun Utara", result.get(0).getNamaKebun());
        }

        @Test
        void findAll_filterByKode_returnsFiltered() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            KebunSawit k2 = createValidKebun("id-2", "KB-0002", 500, 0, 200);
            when(repository.findAll()).thenReturn(List.of(k1, k2));

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

            KebunMandorEntity mandorAssignment = new KebunMandorEntity("ma-1", "id-1", 10L);
            when(kebunMandorRepository.findByKebunId("id-1")).thenReturn(Optional.of(mandorAssignment));
            when(userReader.findUserById(10L)).thenReturn(Optional.of(
                    new UserSnapshot(10L, "Pak Mandor", "mandor1", "MANDOR", "CERT-001")));

            KebunSupirEntity supir1 = new KebunSupirEntity("sa-1", "id-1", 20L);
            KebunSupirEntity supir2 = new KebunSupirEntity("sa-2", "id-1", 21L);
            when(kebunSupirRepository.findAllByKebunId("id-1")).thenReturn(List.of(supir1, supir2));
            when(userReader.findUsersByIds(List.of(20L, 21L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Supir Andi", "supir1", "SUPIR", null),
                    new UserSnapshot(21L, "Supir Budi", "supir2", "SUPIR", null)));

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
            when(kebunMandorRepository.findByKebunId("id-1")).thenReturn(Optional.empty());
            when(kebunSupirRepository.findAllByKebunId("id-1")).thenReturn(List.of());

            KebunDetailResponse detail = service.getDetail("id-1", null);

            assertNull(detail.getMandor());
            assertTrue(detail.getSupirList().isEmpty());
        }

        @Test
        void getDetail_filterSupirByName_shouldReturnFiltered() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(kebunMandorRepository.findByKebunId("id-1")).thenReturn(Optional.empty());

            KebunSupirEntity supir1 = new KebunSupirEntity("sa-1", "id-1", 20L);
            KebunSupirEntity supir2 = new KebunSupirEntity("sa-2", "id-1", 21L);
            when(kebunSupirRepository.findAllByKebunId("id-1")).thenReturn(List.of(supir1, supir2));
            when(userReader.findUsersByIds(List.of(20L, 21L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Andi Supir", "supir1", "SUPIR", null),
                    new UserSnapshot(21L, "Budi Driver", "supir2", "SUPIR", null)));

            KebunDetailResponse detail = service.getDetail("id-1", "Andi");

            assertEquals(1, detail.getSupirList().size());
            assertEquals("Andi Supir", detail.getSupirList().get(0).getFullname());
        }

        @Test
        void getDetail_filterSupirByName_handlesNullFullname() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(kebunMandorRepository.findByKebunId("id-1")).thenReturn(Optional.empty());

            KebunSupirEntity supir1 = new KebunSupirEntity("sa-1", "id-1", 20L);
            when(kebunSupirRepository.findAllByKebunId("id-1")).thenReturn(List.of(supir1));
            when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                    new UserSnapshot(20L, null, "supir1", "SUPIR", null)));

            KebunDetailResponse detail = service.getDetail("id-1", "Andi");
            assertTrue(detail.getSupirList().isEmpty());
        }

        @Test
        void getDetail_filterSupirByEmptyString_returnsAll() {
            KebunSawit kebun = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findById("id-1")).thenReturn(Optional.of(kebun));
            when(kebunMandorRepository.findByKebunId("id-1")).thenReturn(Optional.empty());

            KebunSupirEntity supir1 = new KebunSupirEntity("sa-1", "id-1", 20L);
            when(kebunSupirRepository.findAllByKebunId("id-1")).thenReturn(List.of(supir1));
            when(userReader.findUsersByIds(List.of(20L))).thenReturn(List.of(
                    new UserSnapshot(20L, "Andi Supir", "supir1", "SUPIR", null)));

            KebunDetailResponse detail = service.getDetail("id-1", "");
            assertEquals(1, detail.getSupirList().size());
        }

        @Test
        void findAll_nullFilters_returnsAll() {
            KebunSawit k1 = createValidKebun("id-1", "KB-0001", 0, 0, 200);
            when(repository.findAll()).thenReturn(List.of(k1));
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
