package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.WageSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WageSettingServiceTest {

    @Mock
    private WageSettingRepository repository;

    @InjectMocks
    private WageSettingServiceImpl service;

    private WageSetting existingSetting;

    @BeforeEach
    void setUp() {
        existingSetting = new WageSetting(
                "DEFAULT",
                new BigDecimal("1.50"),
                new BigDecimal("0.75"),
                new BigDecimal("2.00")
        );
    }

    @Test
    void testGetWageSetting_Exists() {
        when(repository.findById("DEFAULT")).thenReturn(Optional.of(existingSetting));
        
        WageSetting result = service.getWageSetting();
        assertEquals("DEFAULT", result.getId());
        assertEquals(new BigDecimal("1.50"), result.getUpahBuruhPerKg());
    }

    @Test
    void testGetWageSetting_NotFound_ShouldCreateDefault() {
        when(repository.findById("DEFAULT")).thenReturn(Optional.empty());
        when(repository.save(any(WageSetting.class))).thenAnswer(i -> i.getArgument(0));

        WageSetting result = service.getWageSetting();
        
        assertEquals("DEFAULT", result.getId());
        assertEquals(BigDecimal.ZERO, result.getUpahBuruhPerKg());
        assertEquals(BigDecimal.ZERO, result.getUpahSupirPerKg());
        assertEquals(BigDecimal.ZERO, result.getUpahMandorPerKg());
        verify(repository, times(1)).save(any(WageSetting.class));
    }

    @Test
    void testUpdateAllWages_Success() {
        when(repository.findById("DEFAULT")).thenReturn(Optional.of(existingSetting));
        when(repository.save(any(WageSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal newUpahBuruh = new BigDecimal("2.50");
        BigDecimal newUpahSupir = new BigDecimal("1.25");
        BigDecimal newUpahMandor = new BigDecimal("3.00");

        WageSetting result = service.updateWages(newUpahBuruh, newUpahSupir, newUpahMandor);

        assertEquals(newUpahBuruh, result.getUpahBuruhPerKg());
        assertEquals(newUpahSupir, result.getUpahSupirPerKg());
        assertEquals(newUpahMandor, result.getUpahMandorPerKg());
        verify(repository, times(1)).save(existingSetting);
    }

    @Test
    void testPartialUpdateWages_OnlySupir() {
        when(repository.findById("DEFAULT")).thenReturn(Optional.of(existingSetting));
        when(repository.save(any(WageSetting.class))).thenAnswer(i -> i.getArgument(0));

        BigDecimal newUpahSupir = new BigDecimal("1.00");

        WageSetting result = service.updateWages(null, newUpahSupir, null);

        assertEquals(new BigDecimal("1.50"), result.getUpahBuruhPerKg());
        assertEquals(newUpahSupir, result.getUpahSupirPerKg());
        assertEquals(new BigDecimal("2.00"), result.getUpahMandorPerKg());
    }
}