package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;

public class UpdateAssignmentStatusRequest {
    private StatusAssignment status;

    public UpdateAssignmentStatusRequest() {
    }

    public UpdateAssignmentStatusRequest(StatusAssignment status) {
        this.status = status;
    }

    public StatusAssignment getStatus() {
        return status;
    }

    public void setStatus(StatusAssignment status) {
        this.status = status;
    }
}
