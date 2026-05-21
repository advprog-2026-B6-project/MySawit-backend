package id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

import java.time.LocalDateTime;

public final class PayrollRequestFactory {
    private PayrollRequestFactory() {
    }

    public static PayrollRequest fromAssignment(PengirimanAssignment assignment,
                                                User mandor,
                                                double muatanKg,
                                                SupirIdentityMapper supirIdentityMapper) {
        return PayrollRequest.builder()
                .pengirimanId(null)
                .supirTrukId(supirIdentityMapper.toSupirId(assignment.getSupirEmail()))
                .mandorId(mandor != null ? mandor.getId() : null)
                .muatanKg(muatanKg)
                .tujuan(assignment.getTujuan())
                .waktuDisetujui(LocalDateTime.now())
                .build();
    }
}
