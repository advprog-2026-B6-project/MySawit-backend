package id.ac.ui.cs.advprog.mysawit.pembayaran.model.state;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;

// DESIGN PATTERN: State Pattern
public class AcceptedState implements PayrollState {
    
    @Override
    public void approve(Payroll payroll) {
        throw new IllegalStateException("Payroll is already accepted.");
    }

    @Override
    public void reject(Payroll payroll, String reason) {
        throw new IllegalStateException("Cannot reject an accepted payroll.");
    }

    @Override
    public String getStatusString() {
        return "ACCEPTED";
    }
}
