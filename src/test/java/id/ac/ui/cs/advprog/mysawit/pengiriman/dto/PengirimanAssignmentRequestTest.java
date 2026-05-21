package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PengirimanAssignmentRequestTest {

    @Test
    void constructorAndGetters() {
        PengirimanAssignmentRequest request =
                new PengirimanAssignmentRequest("mandor@mysawit.id", "supir@mysawit.id", 100.0, "Pabrik A");

        assertEquals("mandor@mysawit.id", request.getMandorEmail());
        assertEquals("supir@mysawit.id", request.getSupirEmail());
        assertEquals(100.0, request.getMuatanKg());
        assertEquals("Pabrik A", request.getTujuan());
    }

    @Test
    void setters() {
        PengirimanAssignmentRequest request = new PengirimanAssignmentRequest();
        request.setMandorEmail("m");
        request.setSupirEmail("s");
        request.setMuatanKg(123.0);
        request.setTujuan("t");

        assertEquals("m", request.getMandorEmail());
        assertEquals("s", request.getSupirEmail());
        assertEquals(123.0, request.getMuatanKg());
        assertEquals("t", request.getTujuan());
    }
}
