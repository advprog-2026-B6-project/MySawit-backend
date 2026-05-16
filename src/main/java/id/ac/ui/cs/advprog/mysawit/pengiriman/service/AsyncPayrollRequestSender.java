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
        sendPayrollRequest(pengiriman, pengiriman.getMuatanKg());
    }

    @Override
    public void sendPayrollRequest(Pengiriman pengiriman, double muatanKgDiakui) {
        PayrollRequest request = buildRequest(pengiriman, muatanKgDiakui);
        CompletableFuture.runAsync(() -> payrollRequestClient.sendPayrollRequest(request));
    }

    private PayrollRequest buildRequest(Pengiriman pengiriman, double muatanKgDiakui) {
        return PayrollRequest.builder()
                .pengirimanId(pengiriman.getId())
                .supirTrukId(pengiriman.getSupirTrukId())
                .mandorId(pengiriman.getMandorId())
                .muatanKg(muatanKgDiakui)
                .tujuan(pengiriman.getTujuan())
                .waktuDisetujui(pengiriman.getWaktuDisetujui())
                .build();
    }
}