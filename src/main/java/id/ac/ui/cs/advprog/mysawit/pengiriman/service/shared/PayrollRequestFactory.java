package id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared;

import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

public abstract class PayrollRequestFactory {

    public PayrollRequest createFromAssignment(PengirimanAssignment assignment,
                                               User mandor,
                                               SupirIdentityMapper supirIdentityMapper) {
        return PayrollRequest.builder()
                .pengirimanId(null)
                .supirTrukId(supirIdentityMapper.toSupirId(assignment.getSupirEmail()))
                .mandorId(mandor != null ? mandor.getId() : null)
                .muatanKg(resolveMuatanKg(assignment))
                .tujuan(assignment.getTujuan())
                .waktuDisetujui(LocalDateTime.now())
                .build();
    }

    protected abstract double resolveMuatanKg(PengirimanAssignment assignment);
}
