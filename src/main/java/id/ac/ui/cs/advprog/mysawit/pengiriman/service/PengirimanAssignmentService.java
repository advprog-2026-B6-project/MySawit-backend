package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;

public interface PengirimanAssignmentService {
    PengirimanAssignmentResponse createAssignment(PengirimanAssignmentRequest request);
    List<PengirimanAssignmentResponse> getAllAssignments();
}
