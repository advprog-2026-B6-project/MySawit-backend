package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

@Service
public class NoOpPayrollRequestClient implements PayrollRequestClient {

    @Override
    public void sendPayrollRequest(PayrollRequest request) {
        // No-op placeholder for integration with payroll service.
    }
}