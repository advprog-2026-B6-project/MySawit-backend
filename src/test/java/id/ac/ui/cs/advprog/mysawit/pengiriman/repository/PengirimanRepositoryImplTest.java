package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PengirimanRepositoryImplTest {

    private PengirimanRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PengirimanRepositoryImpl();
    }

    @Test
    void testSave() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();

        Pengiriman saved = repository.save(pengiriman);

        assertNotNull(saved);
        assertEquals(pengiriman.getId(), saved.getId());
    }

    @Test
    void testFindByIdFound() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();
        repository.save(pengiriman);

        Optional<Pengiriman> found = repository.findById(pengiriman.getId());

        assertTrue(found.isPresent());
        assertEquals(pengiriman.getId(), found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Pengiriman> found = repository.findById(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        Pengiriman pengiriman1 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .build();
        Pengiriman pengiriman2 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik B")
                .build();

        repository.save(pengiriman1);
        repository.save(pengiriman2);

        List<Pengiriman> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testFindAllEmpty() {
        List<Pengiriman> all = repository.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void testFindBySupirTrukId() {
        UUID supirTrukId = UUID.randomUUID();
        Pengiriman pengiriman1 = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .build();
        Pengiriman pengiriman2 = Pengiriman.builder()
                .supirTrukId(supirTrukId)
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik B")
                .build();
        Pengiriman pengiriman3 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(100.0)
                .tujuan("Pabrik C")
                .build();

        repository.save(pengiriman1);
        repository.save(pengiriman2);
        repository.save(pengiriman3);

        List<Pengiriman> found = repository.findBySupirTrukId(supirTrukId);

        assertEquals(2, found.size());
    }

    @Test
    void testFindBySupirTrukIdNoMatch() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .build();
        repository.save(pengiriman);

        List<Pengiriman> found = repository.findBySupirTrukId(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllSedangBerlangsung() {
        Pengiriman pengiriman1 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .build();
        pengiriman1.setStatus(StatusPengiriman.MEMUAT);

        Pengiriman pengiriman2 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik B")
                .build();
        pengiriman2.setStatus(StatusPengiriman.MENGIRIM);

        Pengiriman pengiriman3 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(100.0)
                .tujuan("Pabrik C")
                .build();
        // Default status MENUNGGU - not berlangsung

        Pengiriman pengiriman4 = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(150.0)
                .tujuan("Pabrik D")
                .build();
        pengiriman4.setStatus(StatusPengiriman.TIBA);

        repository.save(pengiriman1);
        repository.save(pengiriman2);
        repository.save(pengiriman3);
        repository.save(pengiriman4);

        List<Pengiriman> berlangsung = repository.findAllSedangBerlangsung();

        assertEquals(2, berlangsung.size());
    }

    @Test
    void testFindAllSedangBerlangsungEmpty() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(200.0)
                .tujuan("Pabrik A")
                .build();
        // Default status MENUNGGU
        repository.save(pengiriman);

        List<Pengiriman> berlangsung = repository.findAllSedangBerlangsung();

        assertTrue(berlangsung.isEmpty());
    }

    @Test
    void testDeleteById() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();
        repository.save(pengiriman);

        repository.deleteById(pengiriman.getId());

        Optional<Pengiriman> found = repository.findById(pengiriman.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByIdNonExistent() {
        // Should not throw exception
        assertDoesNotThrow(() -> repository.deleteById(UUID.randomUUID()));
    }

    @Test
    void testUpdateExistingPengiriman() {
        Pengiriman pengiriman = Pengiriman.builder()
                .supirTrukId(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .muatanKg(300.0)
                .tujuan("Pabrik A")
                .build();
        repository.save(pengiriman);

        pengiriman.setMuatanKg(400.0);
        pengiriman.setTujuan("Pabrik B");
        repository.save(pengiriman);

        Optional<Pengiriman> found = repository.findById(pengiriman.getId());
        assertTrue(found.isPresent());
        assertEquals(400.0, found.get().getMuatanKg());
        assertEquals("Pabrik B", found.get().getTujuan());
        assertEquals(1, repository.findAll().size());
    }
}
