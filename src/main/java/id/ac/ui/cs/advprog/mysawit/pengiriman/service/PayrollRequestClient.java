package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

public interface PayrollRequestClient {
    void sendPayrollRequest(PayrollRequest request);
}