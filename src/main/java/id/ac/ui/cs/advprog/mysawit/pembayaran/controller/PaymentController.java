package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.MidtransNotification;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/pembayaran")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PayrollRepository payrollRepository;

    @Value("${midtrans.server.key}")
    private String serverKey;

    public PaymentController(PaymentService paymentService,
                             PayrollRepository payrollRepository) {
        this.paymentService = paymentService;
        this.payrollRepository = payrollRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        CheckoutResponse response = paymentService.createSnapToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/webhook", consumes = "application/json")
    public ResponseEntity<String> handleMidtransNotification(@RequestBody MidtransNotification notification) {
        logger.info("Received Midtrans notification for Order ID: {}", notification.getOrderId());

        String orderIdStr = notification.getOrderId();
        String statusCode = notification.getStatusCode();

        String grossAmount = notification.getGrossAmount();

        String signatureInput = orderIdStr + statusCode + grossAmount + serverKey;
        String generatedSignature = calculateSHA512(signatureInput);

        if (!generatedSignature.equals(notification.getSignatureKey())) {
            logger.warn("Invalid signature key for Order ID: {}", orderIdStr);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature key");
        }

        String transactionStatus = notification.getTransactionStatus();
        String fraudStatus = notification.getFraudStatus();

        try {
            Long payrollId = Long.parseLong(orderIdStr);
            Optional<Payroll> payrollOpt = payrollRepository.findById(payrollId);

            if (payrollOpt.isEmpty()) {
                logger.error("Payroll not found for Order ID: {}", orderIdStr);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll not found");
            }

            Payroll payroll = payrollOpt.get();

            if ("capture".equals(transactionStatus) || "settlement".equals(transactionStatus)) {
                if ("challenge".equals(fraudStatus)) {
                    logger.info("Transaction for Order ID {} is challenged.", orderIdStr);
                } else {
                    payroll.setStatus("PAID");
                    payrollRepository.save(payroll);
                    logger.info("Payroll {} is marked as PAID.", orderIdStr);
                }
            } else if ("cancel".equals(transactionStatus) ||
                    "deny".equals(transactionStatus) ||
                    "expire".equals(transactionStatus)) {
                payroll.setStatus("FAILED");
                payrollRepository.save(payroll);
                logger.info("Transaction for Order ID {} failed.", orderIdStr);
            }

            return ResponseEntity.ok("OK");

        } catch (NumberFormatException e) {
            logger.error("Invalid Order ID format: {}", orderIdStr, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Order ID format");
        } catch (Exception e) {
            logger.error("Error processing transaction status for Order ID {}", orderIdStr, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    private String calculateSHA512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
    }
}
