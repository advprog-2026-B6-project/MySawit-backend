package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.WageUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.WageSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WageSettingControllerTest {

    @Mock
    private WageSettingService wageSettingService;

    @InjectMocks
    private WageSettingController wageSettingController;

    private WageSetting wageSetting;

    @BeforeEach
    void setUp() {
        wageSetting = new WageSetting();
        wageSetting.setId("1");
        wageSetting.setUpahBuruhPerKg(new BigDecimal("100"));
        wageSetting.setUpahSupirPerKg(new BigDecimal("50"));
        wageSetting.setUpahMandorPerKg(new BigDecimal("200"));
    }

    @Test
    void testGetWages() {
        when(wageSettingService.getWageSetting()).thenReturn(wageSetting);

        ResponseEntity<WageSetting> response = wageSettingController.getWages();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(new BigDecimal("100"), response.getBody().getUpahBuruhPerKg());
    }

    @Test
    void testUpdateWages() {
        WageUpdateRequest request = new WageUpdateRequest();
        request.setUpahBuruhPerKg(new BigDecimal("200"));

        when(wageSettingService.updateWages(any(), any(), any())).thenReturn(wageSetting);

        ResponseEntity<WageSetting> response = wageSettingController.updateWages(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(new BigDecimal("200"),
                response.getBody().getUpahMandorPerKg()); // Just testing that body mapping succeeds
    }
}
