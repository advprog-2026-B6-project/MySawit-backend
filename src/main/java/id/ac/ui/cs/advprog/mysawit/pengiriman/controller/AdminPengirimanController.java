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
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
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
        try {
            List<ApprovedPengirimanResponse> pengirimanList = pengirimanService
                    .getPengirimanDisetujui(mandorName, tanggalMulai, tanggalSelesai);
            return ResponseEntity.ok(
                    ApiResponse.success("Daftar pengiriman disetujui berhasil diambil", pengirimanList));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Pengiriman>> approvePengirimanFinal(
        @PathVariable UUID id,
            @RequestBody AdminApprovePengirimanRequest request) {
        try {
            Pengiriman pengiriman = pengirimanService.setujuiPengirimanAdmin(id, request.getAdminId());
            return ResponseEntity.ok(
                    ApiResponse.success("Pengiriman final berhasil disetujui", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Pengiriman>> rejectPengirimanFinal(
        @PathVariable UUID id,
            @RequestBody AdminRejectPengirimanRequest request) {
        try {
            Pengiriman pengiriman = pengirimanService.tolakPengirimanAdmin(
                    id, request.getAdminId(), request.getAlasanPenolakan());
            return ResponseEntity.ok(
                    ApiResponse.success("Pengiriman final berhasil ditolak", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject-partial")
    public ResponseEntity<ApiResponse<Pengiriman>> rejectPengirimanFinalParsial(
        @PathVariable UUID id,
            @RequestBody PartialRejectPengirimanRequest request) {
        try {
            Pengiriman pengiriman = pengirimanService.tolakPengirimanParsialAdmin(
                    id,
                    request.getAdminId(),
                    request.getMuatanKgDiakui(),
                    request.getAlasanPenolakan());
            return ResponseEntity.ok(
                    ApiResponse.success("Pengiriman final berhasil ditolak parsial", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
