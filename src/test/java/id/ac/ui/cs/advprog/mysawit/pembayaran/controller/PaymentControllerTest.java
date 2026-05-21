package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.MidtransNotification;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PayrollRepository payrollRepository;

    @InjectMocks
    private PaymentController paymentController;

    private Payroll mockPayroll;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        // Menyuntikkan nilai mock-server-key ke variabel private serverKey di controller
        ReflectionTestUtils.setField(paymentController, "serverKey", "mock-server-key");

        mockPayroll = Payroll.builder()
                .id(1L)
                .username("workerA")
                .totalWage(new BigDecimal("10000.00"))
                .status("PENDING")
                .build();
    }

    @Test
    void testCheckoutSuccess() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("10000.00"));
        request.setCustomerName("Budi");
        request.setCustomerEmail("budi@example.com");

        CheckoutResponse mockResponse = new CheckoutResponse("token-123", "url-123");
        when(paymentService.createSnapToken(any(CheckoutRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/pembayaran/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-123"))
                .andExpect(jsonPath("$.redirectUrl").value("url-123"));
    }

    @Test
    void testHandleWebhookValidSignatureSettlement() throws Exception {
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));

        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("1");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("settlement");
        notification.setFraudStatus("accept");

        String expectedSignature = getExpectedSignature("1", "200", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        assertEquals("PAID", mockPayroll.getStatus());
        verify(payrollRepository, times(1)).save(mockPayroll);
    }

    @Test
    void testHandleWebhookValidSignatureDeny() throws Exception {
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));

        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("1");
        notification.setStatusCode("202");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("deny");

        String expectedSignature = getExpectedSignature("1", "202", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        assertEquals("FAILED", mockPayroll.getStatus());
        verify(payrollRepository, times(1)).save(mockPayroll);
    }

    @Test
    void testHandleWebhookInvalidSignature() throws Exception {
        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("1");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("settlement");
        notification.setSignatureKey("invalid-signature");

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid signature key"));

        verify(payrollRepository, never()).save(any());
    }

    @Test
    void testHandleWebhookInvalidOrderIdFormat() throws Exception {
        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("invalid-id");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("settlement");

        String expectedSignature = getExpectedSignature("invalid-id", "200", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Order ID format"));

        verify(payrollRepository, never()).save(any());
    }

    @Test
    void testHandleWebhookPayrollNotFound() throws Exception {
        when(payrollRepository.findById(99L)).thenReturn(Optional.empty());

        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("99");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("settlement");

        String expectedSignature = getExpectedSignature("99", "200", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payroll not found"));

        verify(payrollRepository, never()).save(any());
    }

    @Test
    void testHandleWebhookValidSignatureCancel() throws Exception {
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));

        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("1");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("cancel");

        String expectedSignature = getExpectedSignature("1", "200", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        assertEquals("FAILED", mockPayroll.getStatus());
        verify(payrollRepository, times(1)).save(mockPayroll);
    }

    @Test
    void testHandleWebhookValidSignatureChallenge() throws Exception {
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(mockPayroll));

        MidtransNotification notification = new MidtransNotification();
        notification.setOrderId("1");
        notification.setStatusCode("200");
        notification.setGrossAmount("10000.00");
        notification.setTransactionStatus("settlement");
        notification.setFraudStatus("challenge");

        String expectedSignature = getExpectedSignature("1", "200", "10000.00", "mock-server-key");
        notification.setSignatureKey(expectedSignature);

        mockMvc.perform(post("/pembayaran/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Status should remain PENDING because it is challenged
        assertEquals("PENDING", mockPayroll.getStatus());
        verify(payrollRepository, never()).save(any());
    }

    private String getExpectedSignature(String orderId, String statusCode, String grossAmount, String serverKey) {
        try {
            String input = orderId + statusCode + grossAmount + serverKey;
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}