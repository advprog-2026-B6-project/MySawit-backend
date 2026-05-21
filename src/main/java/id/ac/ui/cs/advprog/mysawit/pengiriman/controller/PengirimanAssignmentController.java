package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.SupirAssignmentProfileResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UpdateAssignmentApprovalRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UpdateAssignmentStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanAssignmentService;

@RestController
@RequestMapping("/api/pengiriman/assignments")
public class PengirimanAssignmentController {

    private final PengirimanAssignmentService assignmentService;
    private final UserRepository userRepository;

    public PengirimanAssignmentController(
            PengirimanAssignmentService assignmentService,
            UserRepository userRepository) {
        this.assignmentService = assignmentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> createAssignment(
            @RequestBody PengirimanAssignmentRequest request) {
        PengirimanAssignmentResponse response = assignmentService.createAssignment(request, getCurrentEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Penugasan pengiriman berhasil dibuat", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getAllAssignments() {
        List<PengirimanAssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(ApiResponse.success("Daftar penugasan pengiriman berhasil diambil", assignments));
    }

    @GetMapping("/me/mandor")
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getAssignmentsMandorSaya() {
        List<PengirimanAssignmentResponse> assignments =
                assignmentService.getAssignmentsByMandorEmail(getCurrentEmail());
        return ResponseEntity.ok(ApiResponse.success("Daftar penugasan mandor berhasil diambil", assignments));
    }

    @GetMapping("/me/supir")
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getAssignmentsSupirSaya() {
        List<PengirimanAssignmentResponse> assignments =
                assignmentService.getAssignmentsBySupirEmail(getCurrentEmail());
        return ResponseEntity.ok(ApiResponse.success("Daftar penugasan supir berhasil diambil", assignments));
    }

    @GetMapping("/me/supir/riwayat")
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getRiwayatAssignmentsSupirSaya(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalMulai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalSelesai) {
        List<PengirimanAssignmentResponse> assignments = assignmentService.getRiwayatAssignmentsBySupirEmail(
                getCurrentEmail(), tanggalMulai, tanggalSelesai);
        return ResponseEntity.ok(ApiResponse.success("Riwayat penugasan supir berhasil diambil", assignments));
    }

    @GetMapping("/me/mandor/supir/{supirId}")
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getAssignmentsSupirByMandor(
            @PathVariable UUID supirId) {
        String mandorEmail = getCurrentEmail();
        String supirEmail = resolveSupirEmailById(supirId);
        List<PengirimanAssignmentResponse> assignments =
                assignmentService.getAssignmentsByMandorAndSupirEmail(mandorEmail, supirEmail);
        return ResponseEntity.ok(ApiResponse.success("Daftar penugasan supir berhasil diambil", assignments));
    }

    @GetMapping("/me/mandor/supir-email/{supirEmail}")
    public ResponseEntity<ApiResponse<SupirAssignmentProfileResponse>> getSupirProfileByEmailForMandor(
            @PathVariable String supirEmail) {
        String mandorEmail = getCurrentEmail();
        var supirUser = userRepository.findByUsername(supirEmail)
                .filter(user -> user.getRole() == Role.SUPIR)
                .orElseThrow(() -> new IllegalArgumentException("Supir tidak ditemukan"));
        List<PengirimanAssignmentResponse> assignments =
                assignmentService.getAssignmentsByMandorAndSupirEmail(mandorEmail, supirEmail);
        SupirAssignmentProfileResponse response = new SupirAssignmentProfileResponse(
                supirUser.getUsername(),
                supirUser.getUsername(),
                assignments);
        return ResponseEntity.ok(ApiResponse.success("Profil supir berhasil diambil", response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateAssignmentStatusRequest request) {
        PengirimanAssignmentResponse response = assignmentService.updateStatus(
                id,
                getCurrentEmail(),
                request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Status penugasan berhasil diubah", response));
    }

    @PutMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> updateApproval(
            @PathVariable Long id,
            @RequestBody UpdateAssignmentApprovalRequest request) {
        PengirimanAssignmentResponse response = assignmentService.updateApproval(
                id, getCurrentEmail(), request.getApproval(), request.getNote());
        return ResponseEntity.ok(ApiResponse.success("Approval penugasan berhasil diperbarui", response));
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Pengguna tidak terautentikasi");
        }
        String username = authentication.getName();
        if (username == null || username.isBlank() || "anonymousUser".equalsIgnoreCase(username)) {
            throw new IllegalStateException("Pengguna tidak terautentikasi");
        }
        return username;
    }

    private String resolveSupirEmailById(UUID supirId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SUPIR)
                .filter(user -> UUID.nameUUIDFromBytes(user.getUsername().getBytes()).equals(supirId))
                .map(user -> user.getUsername())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Supir tidak ditemukan"));
    }
}
