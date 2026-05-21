package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PayrollRequest;

class NoOpPayrollRequestClientTest {

    @Test
    void sendPayrollRequest_doesNothing() {
        NoOpPayrollRequestClient client = new NoOpPayrollRequestClient();
        client.sendPayrollRequest(new PayrollRequest());
    }
}
