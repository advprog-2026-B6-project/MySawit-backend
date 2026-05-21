package id.ac.ui.cs.advprog.mysawit.pengiriman.service.assignment;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PayrollRequestSender;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanNotFoundException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanStateException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PayrollRequestFactory;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.PengirimanValidationRules;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PengirimanAssignmentAdminService {
    private final PengirimanAssignmentRepository pengirimanAssignmentRepository;
    private final UserRepository userRepository;
    private final PayrollRequestSender payrollRequestSender;
    private final SupirIdentityMapper supirIdentityMapper;
    private final PayrollRequestFactory fullPayrollRequestFactory;
    private final PayrollRequestFactory partialPayrollRequestFactory;

    public PengirimanAssignmentAdminService(PengirimanAssignmentRepository pengirimanAssignmentRepository,
                                            UserRepository userRepository,
                                            PayrollRequestSender payrollRequestSender,
                                            SupirIdentityMapper supirIdentityMapper,
                                            @Qualifier("fullPayrollRequestFactory")
                                            PayrollRequestFactory fullPayrollRequestFactory,
                                            @Qualifier("partialPayrollRequestFactory")
                                            PayrollRequestFactory partialPayrollRequestFactory) {
        this.pengirimanAssignmentRepository = pengirimanAssignmentRepository;
        this.userRepository = userRepository;
        this.payrollRequestSender = payrollRequestSender;
        this.supirIdentityMapper = supirIdentityMapper;
        this.fullPayrollRequestFactory = fullPayrollRequestFactory;
        this.partialPayrollRequestFactory = partialPayrollRequestFactory;
    }

    public List<ApprovedPengirimanResponse> getPengirimanDisetujui(String mandorName,
                                                                    LocalDate tanggalMulai,
                                                                    LocalDate tanggalSelesai) {
        PengirimanValidationRules.validateDateRange(tanggalMulai, tanggalSelesai);
        String normalizedMandorQuery = mandorName == null ? "" : mandorName.trim().toLowerCase();
        LocalDateTime startAt = tanggalMulai == null ? null : tanggalMulai.atStartOfDay();
        LocalDateTime endAt = tanggalSelesai == null ? null : tanggalSelesai.atTime(LocalTime.MAX);

        List<PengirimanAssignment> approvedAssignments = pengirimanAssignmentRepository.findApprovedAssignmentsForAdmin(
                        ApprovalAssignment.APPROVED,
                        normalizedMandorQuery,
                        startAt,
                        endAt);
        if (approvedAssignments.isEmpty()) {
            approvedAssignments = pengirimanAssignmentRepository.findAll().stream()
                    .filter(a -> a.getApproval() == ApprovalAssignment.APPROVED)
                    .filter(a -> normalizedMandorQuery.isBlank()
                            || (a.getMandorEmail() == null ? "" : a.getMandorEmail().toLowerCase())
                            .contains(normalizedMandorQuery))
                    .filter(a -> {
                        if (startAt == null && endAt == null) {
                            return true;
                        }
                        if (a.getCreatedAt() == null) {
                            return false;
                        }
                        return (startAt == null || !a.getCreatedAt().isBefore(startAt))
                                && (endAt == null || !a.getCreatedAt().isAfter(endAt));
                    })
                    .toList();
        }

        return approvedAssignments.stream()
                .map(this::toApprovedResponseFromAssignment)
                .collect(Collectors.toList());
    }

    @Transactional
    public PengirimanAssignment setujuiAssignmentFinalAdmin(Long assignmentId, Long adminId) {
        validateAdmin(adminId);
        PengirimanAssignment assignment = findAssignmentById(assignmentId);
        validateAssignmentMutableAndApproved(assignment);

        assignment.setAdminFinalApproval(ApprovalAssignment.APPROVED);
        assignment.setAdminFinalNote(null);
        assignment.setAdminFinalReviewedAt(LocalDateTime.now());
        PengirimanAssignment saved = pengirimanAssignmentRepository.save(assignment);
        sendFullPayrollRequestForAssignment(saved);
        return saved;
    }

    @Transactional
    public PengirimanAssignment tolakAssignmentFinalAdmin(Long assignmentId, Long adminId, String alasanPenolakan) {
        validateAdmin(adminId);
        PengirimanAssignment assignment = findAssignmentById(assignmentId);
        validateAssignmentMutableAndApproved(assignment);

        String normalizedReason = PengirimanValidationRules.normalizeRequiredReason(alasanPenolakan);
        assignment.setAdminFinalApproval(ApprovalAssignment.REJECTED);
        assignment.setAdminFinalNote(normalizedReason);
        assignment.setKilogramDiakui(null);
        assignment.setAdminFinalReviewedAt(LocalDateTime.now());
        return pengirimanAssignmentRepository.save(assignment);
    }

    @Transactional
    public PengirimanAssignment tolakAssignmentFinalParsialAdmin(Long assignmentId, Long adminId, double muatanKgDiakui,
                                                                 String alasanPenolakan) {
        validateAdmin(adminId);
        PengirimanAssignment assignment = findAssignmentById(assignmentId);
        validateAssignmentMutableAndApproved(assignment);
        PengirimanValidationRules.validateMuatanDiakui(muatanKgDiakui, assignment.getMuatanKg());

        String normalizedReason = PengirimanValidationRules.normalizeRequiredReason(alasanPenolakan);
        assignment.setAdminFinalApproval(ApprovalAssignment.PARTIALLY_REJECTED);
        assignment.setAdminFinalNote(normalizedReason);
        assignment.setKilogramDiakui(muatanKgDiakui);
        assignment.setAdminFinalReviewedAt(LocalDateTime.now());

        PengirimanAssignment saved = pengirimanAssignmentRepository.save(assignment);
        sendPartialPayrollRequestForAssignment(saved);
        return saved;
    }

    private ApprovedPengirimanResponse toApprovedResponseFromAssignment(PengirimanAssignment assignment) {
        User mandor = userRepository.findByUsername(assignment.getMandorEmail()).orElse(null);
        String mandorFullname = mandor != null && mandor.getFullname() != null ? mandor.getFullname().trim() : "";
        String mandorDisplayName = !mandorFullname.isBlank() ? mandorFullname : assignment.getMandorEmail();

        ApprovedPengirimanResponse response = new ApprovedPengirimanResponse(
                assignment.getId(),
                UUID.nameUUIDFromBytes(("assignment-" + assignment.getId()).getBytes()),
                supirIdentityMapper.toSupirId(assignment.getSupirEmail()),
                mandor != null ? mandor.getId() : null,
                mandorDisplayName,
                assignment.getMuatanKg(),
                assignment.getTujuan(),
                assignment.getCreatedAt(),
                StatusPengiriman.DISETUJUI
        );
        response.setAdminFinalApproval(assignment.getAdminFinalApproval());
        response.setAdminFinalNote(assignment.getAdminFinalNote());
        response.setKilogramDiakui(assignment.getKilogramDiakui());
        response.setAdminFinalReviewedAt(assignment.getAdminFinalReviewedAt());
        response.setSupirEmail(assignment.getSupirEmail());
        return response;
    }

    private void sendFullPayrollRequestForAssignment(PengirimanAssignment assignment) {
        User mandor = userRepository.findByUsername(assignment.getMandorEmail()).orElse(null);
        var request = fullPayrollRequestFactory.createFromAssignment(assignment, mandor, supirIdentityMapper);
        payrollRequestSender.sendPayrollRequest(request);
    }

    private void sendPartialPayrollRequestForAssignment(PengirimanAssignment assignment) {
        User mandor = userRepository.findByUsername(assignment.getMandorEmail()).orElse(null);
        var request = partialPayrollRequestFactory.createFromAssignment(assignment, mandor, supirIdentityMapper);
        payrollRequestSender.sendPayrollRequest(request);
    }

    private void validateAdmin(Long adminId) {
        if (adminId == null) {
            throw new PengirimanNotFoundException("Admin tidak ditemukan");
        }
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new PengirimanNotFoundException("Admin tidak ditemukan"));
        if (admin.getRole() != Role.ADMIN) {
            throw new PengirimanAuthorizationException("User dengan id " + adminId + " bukan seorang Admin");
        }
    }

    private PengirimanAssignment findAssignmentById(Long assignmentId) {
        return pengirimanAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new PengirimanNotFoundException("Penugasan pengiriman tidak ditemukan"));
    }

    private void validateAssignmentMutableAndApproved(PengirimanAssignment assignment) {
        PengirimanValidationRules.validateAssignmentApprovedByMandor(assignment);
        if (assignment.getAdminFinalApproval() != null) {
            throw new PengirimanStateException("Keputusan final admin sudah dibuat dan tidak dapat diubah");
        }
    }
}
