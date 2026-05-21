package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

import java.time.LocalDateTime;
import java.util.UUID;

public final class PayrollRequestFactory {
    private PayrollRequestFactory() {
    }

    public static PayrollRequest fromAssignment(PengirimanAssignment assignment, User mandor, double muatanKg) {
        UUID supirTrukId = UUID.nameUUIDFromBytes(assignment.getSupirEmail().getBytes());
        return PayrollRequest.builder()
                .pengirimanId(null)
                .supirTrukId(supirTrukId)
                .mandorId(mandor != null ? mandor.getId() : null)
                .muatanKg(muatanKg)
                .tujuan(assignment.getTujuan())
                .waktuDisetujui(LocalDateTime.now())
                .build();
    }
}
