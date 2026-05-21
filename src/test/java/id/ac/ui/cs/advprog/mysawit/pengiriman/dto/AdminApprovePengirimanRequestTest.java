package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AdminApprovePengirimanRequestTest {

    @Test
    void testAdminApprovePengirimanRequestConstructorAndGetter() {
        AdminApprovePengirimanRequest request = new AdminApprovePengirimanRequest(10L);

        assertEquals(10L, request.getAdminId());
    }

    @Test
    void testAdminApprovePengirimanRequestSetter() {
        AdminApprovePengirimanRequest request = new AdminApprovePengirimanRequest();
        request.setAdminId(5L);

        assertEquals(5L, request.getAdminId());
    }
}
