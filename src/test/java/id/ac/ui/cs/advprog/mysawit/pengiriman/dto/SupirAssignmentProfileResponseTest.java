package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupirAssignmentProfileResponseTest {

    @Test
    void constructorAndGetters() {
        List<PengirimanAssignmentResponse> assignments = List.of(new PengirimanAssignmentResponse());
        SupirAssignmentProfileResponse response =
                new SupirAssignmentProfileResponse("supir", "supir@mysawit.id", assignments);

        assertEquals("supir", response.getUsername());
        assertEquals("supir@mysawit.id", response.getEmail());
        assertEquals(assignments, response.getAssignments());
    }

    @Test
    void setters() {
        SupirAssignmentProfileResponse response = new SupirAssignmentProfileResponse();
        List<PengirimanAssignmentResponse> assignments = List.of(new PengirimanAssignmentResponse());
        response.setUsername("u");
        response.setEmail("e");
        response.setAssignments(assignments);

        assertEquals("u", response.getUsername());
        assertEquals("e", response.getEmail());
        assertEquals(assignments, response.getAssignments());
    }
}
