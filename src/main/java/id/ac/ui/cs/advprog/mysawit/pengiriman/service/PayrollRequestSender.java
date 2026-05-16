package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;

public interface PayrollRequestSender {
    void sendPayrollRequest(Pengiriman pengiriman);

    void sendPayrollRequest(Pengiriman pengiriman, double muatanKgDiakui);
}