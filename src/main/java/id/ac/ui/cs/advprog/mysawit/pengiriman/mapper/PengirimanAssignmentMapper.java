package id.ac.ui.cs.advprog.mysawit.pengiriman.mapper;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

public final class PengirimanAssignmentMapper {

    private PengirimanAssignmentMapper() {
    }

    public static PengirimanAssignment toEntity(PengirimanAssignmentRequest request) {
        return PengirimanAssignment.builder()
                .mandorEmail(request.getMandorEmail())
                .supirEmail(request.getSupirEmail())
                .muatanKg(request.getMuatanKg())
                .tujuan(request.getTujuan())
                .build();
    }

    public static PengirimanAssignmentResponse toResponse(PengirimanAssignment assignment) {
        return new PengirimanAssignmentResponse(
                assignment.getId(),
                assignment.getMandorEmail(),
                assignment.getSupirEmail(),
                assignment.getMuatanKg(),
                assignment.getTujuan(),
                assignment.getCreatedAt());
    }
}
