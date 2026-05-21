package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.mapper.PengirimanAssignmentMapper;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PayrollRequestFactory;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PengirimanValidationRules;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PengirimanAssignmentServiceImpl implements PengirimanAssignmentService {

    private final PengirimanAssignmentRepository repository;
    private final UserRepository userRepository;
    private final PayrollRequestSender payrollRequestSender;
    private final SupirIdentityMapper supirIdentityMapper;

    public PengirimanAssignmentServiceImpl(PengirimanAssignmentRepository repository,
                                           UserRepository userRepository,
                                           PayrollRequestSender payrollRequestSender,
                                           SupirIdentityMapper supirIdentityMapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.payrollRequestSender = payrollRequestSender;
        this.supirIdentityMapper = supirIdentityMapper;
    }

    @Override
    @Transactional
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
    public List<PengirimanAssignmentResponse> getRiwayatAssignmentsBySupirEmail(
            String supirEmail, LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        PengirimanValidationRules.validateDateRange(tanggalMulai, tanggalSelesai);

        return repository.findBySupirEmail(supirEmail).stream()
                .filter(a -> a.getApproval() != null || a.getStatus() == StatusAssignment.TIBA)
                .filter(a -> {
                    if (tanggalMulai == null && tanggalSelesai == null) return true;
                    LocalDate tanggal = a.getCreatedAt().toLocalDate();
                    boolean afterStart = tanggalMulai == null || !tanggal.isBefore(tanggalMulai);
                    boolean beforeEnd = tanggalSelesai == null || !tanggal.isAfter(tanggalSelesai);
                    return afterStart && beforeEnd;
                })
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PengirimanAssignmentResponse> getAssignmentsByMandorAndSupirEmail(
            String mandorEmail,
            String supirEmail) {
        return repository.findByMandorEmailAndSupirEmail(mandorEmail, supirEmail).stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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
    @Transactional
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
        if (approval == ApprovalAssignment.APPROVED) {
            sendPayrollRequestForAssignment(saved);
        }
        return PengirimanAssignmentMapper.toResponse(saved);
    }

    private void sendPayrollRequestForAssignment(PengirimanAssignment assignment) {
        User mandor = userRepository.findByUsername(assignment.getMandorEmail()).orElse(null);
        var request = PayrollRequestFactory.fromAssignment(
                assignment, mandor, assignment.getMuatanKg(), supirIdentityMapper);

        payrollRequestSender.sendPayrollRequest(request);
    }

    private void validateRequest(PengirimanAssignmentRequest request, String mandorEmail) {
        if (mandorEmail == null || mandorEmail.isBlank()) {
            throw new IllegalArgumentException("Mandor tidak ditemukan");
        }
        if (request.getSupirEmail() == null || request.getSupirEmail().isBlank()) {
            throw new IllegalArgumentException("Supir email wajib diisi");
        }
        PengirimanValidationRules.validateMuatanAssignment(request.getMuatanKg());
        if (request.getTujuan() == null || request.getTujuan().isBlank()) {
            throw new IllegalArgumentException("Tujuan wajib diisi");
        }
    }
}
