package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

public interface PayrollRequestSender {
    void sendPayrollRequest(Pengiriman pengiriman);

    void sendPayrollRequest(Pengiriman pengiriman, double muatanKgDiakui);

    void sendPayrollRequest(PayrollRequest request);
}
