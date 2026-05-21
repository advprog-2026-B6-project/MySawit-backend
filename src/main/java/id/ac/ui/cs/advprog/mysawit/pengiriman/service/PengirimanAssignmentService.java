package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.time.LocalDate;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;

public interface PengirimanAssignmentService {
    PengirimanAssignmentResponse createAssignment(PengirimanAssignmentRequest request, String mandorEmail);
    List<PengirimanAssignmentResponse> getAllAssignments();
    List<PengirimanAssignmentResponse> getAssignmentsByMandorEmail(String mandorEmail);
    List<PengirimanAssignmentResponse> getAssignmentsBySupirEmail(String supirEmail);
    List<PengirimanAssignmentResponse> getRiwayatAssignmentsBySupirEmail(
            String supirEmail,
            LocalDate tanggalMulai,
            LocalDate tanggalSelesai);
    List<PengirimanAssignmentResponse> getAssignmentsByMandorAndSupirEmail(String mandorEmail, String supirEmail);
    PengirimanAssignmentResponse updateStatus(Long assignmentId, String supirEmail, StatusAssignment status);
    PengirimanAssignmentResponse updateApproval(
            Long assignmentId,
            String mandorEmail,
            ApprovalAssignment approval,
            String note);
}
