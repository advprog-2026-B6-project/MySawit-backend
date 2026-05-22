package id.ac.ui.cs.advprog.mysawit.pembayaran.model.state;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;

// DESIGN PATTERN: State Pattern
public interface PayrollState {
    void approve(Payroll payroll);
    void reject(Payroll payroll, String reason);
    String getStatusString();
}
