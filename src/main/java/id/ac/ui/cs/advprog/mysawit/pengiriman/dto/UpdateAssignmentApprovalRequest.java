package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;

public class UpdateAssignmentApprovalRequest {
    private ApprovalAssignment approval;
    private String note;

    public UpdateAssignmentApprovalRequest() {
    }

    public UpdateAssignmentApprovalRequest(ApprovalAssignment approval, String note) {
        this.approval = approval;
        this.note = note;
    }

    public ApprovalAssignment getApproval() {
        return approval;
    }

    public void setApproval(ApprovalAssignment approval) {
        this.approval = approval;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
