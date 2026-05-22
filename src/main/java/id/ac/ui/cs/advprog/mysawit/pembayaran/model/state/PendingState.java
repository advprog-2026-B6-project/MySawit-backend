package id.ac.ui.cs.advprog.mysawit.pembayaran.model.state;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;

// DESIGN PATTERN: State Pattern
public class PendingState implements PayrollState {
    
    @Override
    public void approve(Payroll payroll) {
        payroll.setStatus("ACCEPTED");
        // State transition handled in service or by resetting state enum if mapped
    }

    @Override
    public void reject(Payroll payroll, String reason) {
        payroll.setStatus("REJECTED");
        payroll.setRejectReason(reason);
    }

    @Override
    public String getStatusString() {
        return "PENDING";
    }
}
