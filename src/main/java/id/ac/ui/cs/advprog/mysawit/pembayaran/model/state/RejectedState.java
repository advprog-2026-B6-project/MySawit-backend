package id.ac.ui.cs.advprog.mysawit.pembayaran.model.state;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;

// DESIGN PATTERN: State Pattern
public class RejectedState implements PayrollState {
    
    @Override
    public void approve(Payroll payroll) {
        throw new IllegalStateException("Cannot approve a rejected payroll.");
    }

    @Override
    public void reject(Payroll payroll, String reason) {
        throw new IllegalStateException("Payroll is already rejected.");
    }

    @Override
    public String getStatusString() {
        return "REJECTED";
    }
}
