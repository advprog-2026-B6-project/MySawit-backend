package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.AcceptedState;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.PayrollState;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.PendingState;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.RejectedState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StateTest {

    @Test
    void testPendingState() {
        Payroll payroll = new Payroll();
        payroll.setStatus("PENDING");
        PayrollState state = new PendingState();

        assertEquals("PENDING", state.getStatusString());

        state.approve(payroll);
        assertEquals("ACCEPTED", payroll.getStatus());

        payroll.setStatus("PENDING"); // reset
        state.reject(payroll, "Reason");
        assertEquals("REJECTED", payroll.getStatus());
        assertEquals("Reason", payroll.getRejectReason());
    }

    @Test
    void testAcceptedState() {
        Payroll payroll = new Payroll();
        payroll.setStatus("ACCEPTED");
        PayrollState state = new AcceptedState();

        assertEquals("ACCEPTED", state.getStatusString());

        assertThrows(IllegalStateException.class, () -> state.approve(payroll));
        assertThrows(IllegalStateException.class, () -> state.reject(payroll, "Reason"));
    }

    @Test
    void testRejectedState() {
        Payroll payroll = new Payroll();
        payroll.setStatus("REJECTED");
        PayrollState state = new RejectedState();

        assertEquals("REJECTED", state.getStatusString());

        assertThrows(IllegalStateException.class, () -> state.approve(payroll));
        assertThrows(IllegalStateException.class, () -> state.reject(payroll, "Reason"));
    }

    @Test
    void testPayrollGetStateLogic() {
        Payroll p = new Payroll();
        p.setStatus("PENDING");
        assertEquals(PendingState.class, p.getPayrollState().getClass());

        p.setStatus("ACCEPTED");
        assertEquals(AcceptedState.class, p.getPayrollState().getClass());

        p.setStatus("REJECTED");
        assertEquals(RejectedState.class, p.getPayrollState().getClass());
    }
}
