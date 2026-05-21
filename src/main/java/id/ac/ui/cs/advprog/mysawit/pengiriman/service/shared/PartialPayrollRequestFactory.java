package id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

@Component("partialPayrollRequestFactory")
public class PartialPayrollRequestFactory extends PayrollRequestFactory {

    @Override
    protected double resolveMuatanKg(PengirimanAssignment assignment) {
        return assignment.getKilogramDiakui() != null
                ? assignment.getKilogramDiakui()
                : assignment.getMuatanKg();
    }
}
