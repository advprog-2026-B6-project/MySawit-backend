package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupirTrukServiceImplTest {

    @Mock
    private SupirTrukRepository supirTrukRepository;

    @InjectMocks
    private SupirTrukServiceImpl supirTrukService;

    private SupirTruk supirTruk;
    private UUID supirTrukId;

    @BeforeEach
    void setUp() {
        supirTrukId = UUID.randomUUID();
        supirTruk = SupirTruk.builder()
                .id(supirTrukId)
                .nama("John Doe")
                .nomorTelepon("081234567890")
                .platNomorTruk("B 1234 ABC")
                .build();
    }

    @Test
    void testGetDaftarSupirBertugas() {
        SupirTruk supir1 = SupirTruk.builder()
                .nama("John")
                .nomorTelepon("08123")
                .platNomorTruk("B 1234")
                .sedangBertugas(true)
                .build();
        SupirTruk supir2 = SupirTruk.builder()
                .nama("Jane")
                .nomorTelepon("08124")
                .platNomorTruk("B 5678")
                .sedangBertugas(true)
                .build();

        when(supirTrukRepository.findAllBertugas()).thenReturn(Arrays.asList(supir1, supir2));

        List<SupirTruk> result = supirTrukService.getDaftarSupirBertugas();

        assertEquals(2, result.size());
        verify(supirTrukRepository).findAllBertugas();
    }

    @Test
    void testGetAllSupirTruk() {
        SupirTruk supir1 = SupirTruk.builder()
                .nama("John")
                .nomorTelepon("08123")
                .platNomorTruk("B 1234")
                .build();
        SupirTruk supir2 = SupirTruk.builder()
                .nama("Jane")
                .nomorTelepon("08124")
                .platNomorTruk("B 5678")
                .build();

        when(supirTrukRepository.findAll()).thenReturn(Arrays.asList(supir1, supir2));

        List<SupirTruk> result = supirTrukService.getAllSupirTruk();

        assertEquals(2, result.size());
        verify(supirTrukRepository).findAll();
    }

    @Test
    void testGetSupirTrukByIdSuccess() {
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));

        SupirTruk result = supirTrukService.getSupirTrukById(supirTrukId);

        assertNotNull(result);
        assertEquals(supirTrukId, result.getId());
        assertEquals("John Doe", result.getNama());
    }

    @Test
    void testGetSupirTrukByIdNotFound() {
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                supirTrukService.getSupirTrukById(supirTrukId));

        assertTrue(exception.getMessage().contains("Supir truk tidak ditemukan"));
    }

    @Test
    void testTambahSupirTruk() {
        when(supirTrukRepository.save(any(SupirTruk.class))).thenReturn(supirTruk);

        SupirTruk result = supirTrukService.tambahSupirTruk(supirTruk);

        assertNotNull(result);
        assertEquals("John Doe", result.getNama());
        verify(supirTrukRepository).save(supirTruk);
    }

    @Test
    void testUpdateStatusBertugas() {
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));
        when(supirTrukRepository.save(any(SupirTruk.class))).thenAnswer(i -> i.getArguments()[0]);

        SupirTruk result = supirTrukService.updateStatusBertugas(supirTrukId, true);

        assertTrue(result.isSedangBertugas());
        verify(supirTrukRepository).save(supirTruk);
    }

    @Test
    void testUpdateStatusBertugasNotFound() {
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                supirTrukService.updateStatusBertugas(supirTrukId, true));

        assertTrue(exception.getMessage().contains("Supir truk tidak ditemukan"));
    }

    @Test
    void testUpdateStatusBertugasToFalse() {
        supirTruk.setSedangBertugas(true);
        when(supirTrukRepository.findById(supirTrukId)).thenReturn(Optional.of(supirTruk));
        when(supirTrukRepository.save(any(SupirTruk.class))).thenAnswer(i -> i.getArguments()[0]);

        SupirTruk result = supirTrukService.updateStatusBertugas(supirTrukId, false);

        assertFalse(result.isSedangBertugas());
        verify(supirTrukRepository).save(supirTruk);
    }
}
