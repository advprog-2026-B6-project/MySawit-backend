package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApprovePengirimanRequestTest {

    @Test
    void testApprovePengirimanRequestConstructorAndGetter() {
        ApprovePengirimanRequest request = new ApprovePengirimanRequest(10L);

        assertEquals(10L, request.getMandorId());
    }

    @Test
    void testApprovePengirimanRequestSetter() {
        ApprovePengirimanRequest request = new ApprovePengirimanRequest();
        request.setMandorId(5L);

        assertEquals(5L, request.getMandorId());
    }
}