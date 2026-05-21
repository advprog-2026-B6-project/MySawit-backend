package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    private PaymentServiceImpl paymentService;
    private MockedStatic<SnapApi> snapApiMockedStatic;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl();
        // Setup static mock for Midtrans SnapApi
        snapApiMockedStatic = Mockito.mockStatic(SnapApi.class);
    }

    @AfterEach
    void tearDown() {
        // Must close the static mock to avoid leaking to other tests
        if (snapApiMockedStatic != null) {
            snapApiMockedStatic.close();
        }
    }

    @Test
    void testCreateSnapTokenSuccess() throws Exception {
        // Arrange
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("10000"));
        request.setCustomerName("Test User");
        request.setCustomerEmail("test@example.com");

        JSONObject mockMidtransResponse = new JSONObject();
        mockMidtransResponse.put("token", "mock-snap-token-123");
        mockMidtransResponse.put("redirect_url", "https://app.sandbox.midtrans.com/snap/v2/vtweb/mock-snap-token-123");

        snapApiMockedStatic.when(() -> SnapApi.createTransaction(any(Map.class)))
                .thenReturn(mockMidtransResponse);

        // Act
        CheckoutResponse response = paymentService.createSnapToken(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock-snap-token-123", response.getToken());
        assertEquals("https://app.sandbox.midtrans.com/snap/v2/vtweb/mock-snap-token-123", response.getRedirectUrl());
    }

    @Test
    void testCreateSnapTokenThrowsMidtransError() {
        // Arrange
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("10000"));

        snapApiMockedStatic.when(() -> SnapApi.createTransaction(any(Map.class)))
                .thenThrow(new MidtransError("API Error"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.createSnapToken(request);
        });

        assertTrue(exception.getMessage().contains("Failed to generate Midtrans Snap Token"));
    }

    @Test
    void testCreateSnapTokenThrowsUnexpectedException() {
        // Arrange
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("10000"));

        snapApiMockedStatic.when(() -> SnapApi.createTransaction(any(Map.class)))
                .thenThrow(new NullPointerException("Unexpected Null"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.createSnapToken(request);
        });

        assertTrue(exception.getMessage().contains("An unexpected error occurred during payment processing."));
    }
}
