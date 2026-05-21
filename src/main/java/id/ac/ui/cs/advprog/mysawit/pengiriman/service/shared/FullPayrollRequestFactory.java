package id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

@Component("fullPayrollRequestFactory")
public class FullPayrollRequestFactory extends PayrollRequestFactory {

    @Override
    protected double resolveMuatanKg(PengirimanAssignment assignment) {
        return assignment.getMuatanKg();
    }
}
