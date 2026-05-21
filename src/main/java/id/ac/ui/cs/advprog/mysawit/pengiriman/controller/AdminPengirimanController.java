package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.AdminApprovePengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.AdminRejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PartialRejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.mapper.PengirimanAssignmentMapper;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;

@RestController
@RequestMapping("/api/admin/pengiriman")
public class AdminPengirimanController {

    private final PengirimanService pengirimanService;

    public AdminPengirimanController(PengirimanService pengirimanService) {
        this.pengirimanService = pengirimanService;
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<ApprovedPengirimanResponse>>> getPengirimanDisetujui(
            @RequestParam(required = false) String mandorName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalMulai,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalSelesai) {
        List<ApprovedPengirimanResponse> pengirimanList = pengirimanService
                .getPengirimanDisetujui(mandorName, tanggalMulai, tanggalSelesai);
        return ResponseEntity.ok(
                ApiResponse.success("Daftar pengiriman disetujui berhasil diambil", pengirimanList));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Pengiriman>> approvePengirimanFinal(
        @PathVariable UUID id,
            @RequestBody AdminApprovePengirimanRequest request) {
        Pengiriman pengiriman = pengirimanService.setujuiPengirimanAdmin(id, request.getAdminId());
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman final berhasil disetujui", pengiriman));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Pengiriman>> rejectPengirimanFinal(
        @PathVariable UUID id,
            @RequestBody AdminRejectPengirimanRequest request) {
        Pengiriman pengiriman = pengirimanService.tolakPengirimanAdmin(
                id, request.getAdminId(), request.getAlasanPenolakan());
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman final berhasil ditolak", pengiriman));
    }

    @PutMapping("/{id}/reject-partial")
    public ResponseEntity<ApiResponse<Pengiriman>> rejectPengirimanFinalParsial(
        @PathVariable UUID id,
            @RequestBody PartialRejectPengirimanRequest request) {
        Pengiriman pengiriman = pengirimanService.tolakPengirimanParsialAdmin(
                id,
                request.getAdminId(),
                request.getMuatanKgDiakui(),
                request.getAlasanPenolakan());
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman final berhasil ditolak parsial", pengiriman));
    }

    @PutMapping("/assignments/{id}/approve")
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> approveAssignmentFinal(
            @PathVariable Long id,
            @RequestBody AdminApprovePengirimanRequest request) {
        PengirimanAssignment assignment = pengirimanService.setujuiAssignmentFinalAdmin(id, request.getAdminId());
        return ResponseEntity.ok(ApiResponse.success(
                "Hasil pengiriman akhir disetujui admin. Payroll mandor diproses async.",
                PengirimanAssignmentMapper.toResponse(assignment)));
    }

    @PutMapping("/assignments/{id}/reject")
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> rejectAssignmentFinal(
            @PathVariable Long id,
            @RequestBody AdminRejectPengirimanRequest request) {
        PengirimanAssignment assignment = pengirimanService.tolakAssignmentFinalAdmin(
                id, request.getAdminId(), request.getAlasanPenolakan());
        return ResponseEntity.ok(ApiResponse.success(
                "Hasil pengiriman akhir ditolak admin.",
                PengirimanAssignmentMapper.toResponse(assignment)));
    }

    @PutMapping("/assignments/{id}/reject-partial")
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> rejectAssignmentFinalParsial(
            @PathVariable Long id,
            @RequestBody PartialRejectPengirimanRequest request) {
        PengirimanAssignment assignment = pengirimanService.tolakAssignmentFinalParsialAdmin(
                id,
                request.getAdminId(),
                request.getMuatanKgDiakui(),
                request.getAlasanPenolakan());
        return ResponseEntity.ok(ApiResponse.success(
                "Hasil pengiriman akhir ditolak parsial admin.",
                PengirimanAssignmentMapper.toResponse(assignment)));
    }
}
