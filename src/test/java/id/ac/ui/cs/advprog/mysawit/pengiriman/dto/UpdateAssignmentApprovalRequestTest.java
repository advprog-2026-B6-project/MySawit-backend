package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateAssignmentApprovalRequestTest {

    @Test
    void constructorAndGetters() {
        UpdateAssignmentApprovalRequest request =
                new UpdateAssignmentApprovalRequest(ApprovalAssignment.APPROVED, "ok");
        assertEquals(ApprovalAssignment.APPROVED, request.getApproval());
        assertEquals("ok", request.getNote());
    }

    @Test
    void setters() {
        UpdateAssignmentApprovalRequest request = new UpdateAssignmentApprovalRequest();
        request.setApproval(ApprovalAssignment.REJECTED);
        request.setNote("reject");
        assertEquals(ApprovalAssignment.REJECTED, request.getApproval());
        assertEquals("reject", request.getNote());
    }
}
