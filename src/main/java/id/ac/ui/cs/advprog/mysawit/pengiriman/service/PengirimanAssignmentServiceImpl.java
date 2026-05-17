package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.mapper.PengirimanAssignmentMapper;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;

@Service
public class PengirimanAssignmentServiceImpl implements PengirimanAssignmentService {

    private final PengirimanAssignmentRepository repository;

    public PengirimanAssignmentServiceImpl(PengirimanAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PengirimanAssignmentResponse createAssignment(PengirimanAssignmentRequest request, String mandorEmail) {
        validateRequest(request, mandorEmail);
        PengirimanAssignment assignment = PengirimanAssignmentMapper.toEntity(request);
        assignment.setMandorEmail(mandorEmail);
        PengirimanAssignment saved = repository.save(assignment);
        return PengirimanAssignmentMapper.toResponse(saved);
    }

    @Override
    public List<PengirimanAssignmentResponse> getAllAssignments() {
        return repository.findAll().stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PengirimanAssignmentResponse> getAssignmentsByMandorEmail(String mandorEmail) {
        return repository.findByMandorEmail(mandorEmail).stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PengirimanAssignmentResponse> getAssignmentsBySupirEmail(String supirEmail) {
        return repository.findBySupirEmail(supirEmail).stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PengirimanAssignmentResponse> getAssignmentsByMandorAndSupirEmail(String mandorEmail, String supirEmail) {
        return repository.findByMandorEmailAndSupirEmail(mandorEmail, supirEmail).stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PengirimanAssignmentResponse updateStatus(Long assignmentId, String supirEmail, StatusAssignment status) {
        if (status == null) {
            throw new IllegalArgumentException("Status wajib diisi");
        }

        PengirimanAssignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Penugasan pengiriman tidak ditemukan"));

        if (!assignment.getSupirEmail().equalsIgnoreCase(supirEmail)) {
            throw new IllegalArgumentException("Anda tidak berhak mengubah status penugasan ini");
        }

        assignment.setStatus(status);
        PengirimanAssignment saved = repository.save(assignment);
        return PengirimanAssignmentMapper.toResponse(saved);
    }

    @Override
    public PengirimanAssignmentResponse updateApproval(
            Long assignmentId, String mandorEmail, ApprovalAssignment approval, String note) {
        if (approval == null) {
            throw new IllegalArgumentException("Approval wajib diisi");
        }

        PengirimanAssignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Penugasan pengiriman tidak ditemukan"));

        if (!assignment.getMandorEmail().equalsIgnoreCase(mandorEmail)) {
            throw new IllegalArgumentException("Anda tidak berhak memberi approval pada penugasan ini");
        }

        if (approval == ApprovalAssignment.REJECTED && (note == null || note.isBlank())) {
            throw new IllegalArgumentException("Note wajib diisi ketika menolak penugasan");
        }

        assignment.setApproval(approval);
        assignment.setNote(approval == ApprovalAssignment.REJECTED ? note.trim() : null);
        PengirimanAssignment saved = repository.save(assignment);
        return PengirimanAssignmentMapper.toResponse(saved);
    }

    private void validateRequest(PengirimanAssignmentRequest request, String mandorEmail) {
        if (mandorEmail == null || mandorEmail.isBlank()) {
            throw new IllegalArgumentException("Mandor tidak ditemukan");
        }
        if (request.getSupirEmail() == null || request.getSupirEmail().isBlank()) {
            throw new IllegalArgumentException("Supir email wajib diisi");
        }
        if (request.getMuatanKg() <= 0 || request.getMuatanKg() > 400) {
            throw new IllegalArgumentException("Muatan harus antara 0 - 400 kg");
        }
        if (request.getTujuan() == null || request.getTujuan().isBlank()) {
            throw new IllegalArgumentException("Tujuan wajib diisi");
        }
    }
}
