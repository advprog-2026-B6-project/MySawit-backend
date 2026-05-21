package id.ac.ui.cs.advprog.mysawit.hasil.payroll;

import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;

public class NoOpHasilPayrollPublisher implements HasilPayrollPublisher {
    @Override
    public void publishApproved(Hasil report) {
        // sengaja kosong buat domain test yang isolated
    }
}
