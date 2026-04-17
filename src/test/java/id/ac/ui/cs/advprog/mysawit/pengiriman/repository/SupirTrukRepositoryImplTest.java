package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupirTrukRepositoryImplTest {

    private SupirTrukRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new SupirTrukRepositoryImpl();
    }

    @Test
    void testSave() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();

        SupirTruk saved = repository.save(supirTruk);

        assertNotNull(saved);
        assertEquals(supirTruk.getId(), saved.getId());
    }

    @Test
    void testFindByIdFound() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        repository.save(supirTruk);

        Optional<SupirTruk> found = repository.findById(supirTruk.getId());

        assertTrue(found.isPresent());
        assertEquals(supirTruk.getId(), found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<SupirTruk> found = repository.findById(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        SupirTruk supirTruk1 = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        SupirTruk supirTruk2 = SupirTruk.builder()
                .nama("Jane Doe")
                .nomorTelepon("081234567891")
                .platNomorTruk("B 5678 DEF")
                .build();

        repository.save(supirTruk1);
        repository.save(supirTruk2);

        List<SupirTruk> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testFindAllEmpty() {
        List<SupirTruk> all = repository.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllBertugas() {
        SupirTruk supirTruk1 = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .sedangBertugas(true)
                .build();
        SupirTruk supirTruk2 = SupirTruk.builder()
                .nama("Jane Doe")
                .nomorTelepon("081234567891")
                .platNomorTruk("B 5678 DEF")
                .sedangBertugas(true)
                .build();
        SupirTruk supirTruk3 = SupirTruk.builder()
                .nama("Bob Smith")
                .nomorTelepon("081234567892")
                .platNomorTruk("B 9012 GHI")
                .sedangBertugas(false)
                .build();

        repository.save(supirTruk1);
        repository.save(supirTruk2);
        repository.save(supirTruk3);

        List<SupirTruk> bertugas = repository.findAllBertugas();

        assertEquals(2, bertugas.size());
    }

    @Test
    void testFindAllBertugasEmpty() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .sedangBertugas(false)
                .build();
        repository.save(supirTruk);

        List<SupirTruk> bertugas = repository.findAllBertugas();

        assertTrue(bertugas.isEmpty());
    }

    @Test
    void testDeleteById() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        repository.save(supirTruk);

        repository.deleteById(supirTruk.getId());

        Optional<SupirTruk> found = repository.findById(supirTruk.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByIdNonExistent() {
        // Should not throw exception
        assertDoesNotThrow(() -> repository.deleteById(UUID.randomUUID()));
    }

    @Test
    void testUpdateExistingSupirTruk() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
        repository.save(supirTruk);

        supirTruk.setNama("John Updated");
        supirTruk.setSedangBertugas(true);
        repository.save(supirTruk);

        Optional<SupirTruk> found = repository.findById(supirTruk.getId());
        assertTrue(found.isPresent());
        assertEquals("John Updated", found.get().getNama());
        assertTrue(found.get().isSedangBertugas());
        assertEquals(1, repository.findAll().size());
    }
}
