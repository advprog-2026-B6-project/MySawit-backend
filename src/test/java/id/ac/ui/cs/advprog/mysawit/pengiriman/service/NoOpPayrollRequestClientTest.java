package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NoOpPayrollRequestClientTest {

    @Test
    void sendPayrollRequest_doesNothing() {
        NoOpPayrollRequestClient client = new NoOpPayrollRequestClient();
        assertDoesNotThrow(() -> client.sendPayrollRequest(new PayrollRequest()));
    }
}
