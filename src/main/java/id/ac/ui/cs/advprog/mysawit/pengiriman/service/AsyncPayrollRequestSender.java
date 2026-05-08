package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;

@Service
public class AsyncPayrollRequestSender implements PayrollRequestSender {

    private final PayrollRequestClient payrollRequestClient;

    public AsyncPayrollRequestSender(PayrollRequestClient payrollRequestClient) {
        this.payrollRequestClient = payrollRequestClient;
    }

    @Override
    public void sendPayrollRequest(Pengiriman pengiriman) {
        PayrollRequest request = PayrollRequest.builder()
                .pengirimanId(pengiriman.getId())
                .supirTrukId(pengiriman.getSupirTrukId())
                .mandorId(pengiriman.getMandorId())
                .muatanKg(pengiriman.getMuatanKg())
                .tujuan(pengiriman.getTujuan())
                .waktuDisetujui(pengiriman.getWaktuDisetujui())
                .build();

        CompletableFuture.runAsync(() -> payrollRequestClient.sendPayrollRequest(request));
    }
}