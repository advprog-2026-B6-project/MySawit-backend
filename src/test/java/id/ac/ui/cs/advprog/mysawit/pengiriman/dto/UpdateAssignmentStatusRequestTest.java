package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateAssignmentStatusRequestTest {

    @Test
    void constructorAndGetters() {
        UpdateAssignmentStatusRequest request = new UpdateAssignmentStatusRequest(StatusAssignment.MENGIRIM);
        assertEquals(StatusAssignment.MENGIRIM, request.getStatus());
    }

    @Test
    void setters() {
        UpdateAssignmentStatusRequest request = new UpdateAssignmentStatusRequest();
        request.setStatus(StatusAssignment.TIBA);
        assertEquals(StatusAssignment.TIBA, request.getStatus());
    }
}
