package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.CheckoutResponse;

public interface PaymentService {
    CheckoutResponse createSnapToken(CheckoutRequest request);
}