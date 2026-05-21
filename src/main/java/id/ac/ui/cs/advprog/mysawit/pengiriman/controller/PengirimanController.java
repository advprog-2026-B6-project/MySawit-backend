package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovePengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.BuatPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.RejectPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UbahStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;

@RestController
@RequestMapping("/api/pengiriman")
public class PengirimanController {

    private final PengirimanService pengirimanService;
    private final UserRepository userRepository;

    public PengirimanController(PengirimanService pengirimanService, UserRepository userRepository) {
        this.pengirimanService = pengirimanService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pengiriman>> buatPengiriman(
            @RequestBody BuatPengirimanRequest request) {
        Long mandorId = resolveMandorId(request.getMandorId());
        Pengiriman pengiriman = pengirimanService.buatPengiriman(
                mandorId,
                request.getSupirTrukId(),
                request.getMuatanKg(),
                request.getTujuan()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pengiriman berhasil dibuat", pengiriman));
    }

    private Long resolveMandorId(Long mandorId) {
        if (mandorId != null) {
            return mandorId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Mandor tidak ditemukan");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mandor tidak ditemukan"));
        return user.getId();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Pengiriman>> ubahStatusPengiriman(
            @PathVariable UUID id,
            @RequestBody UbahStatusRequest request) {
        Pengiriman pengiriman = pengirimanService.ubahStatusPengiriman(
                id,
                request.getSupirTrukId(),
                request.getStatusBaru()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Status pengiriman berhasil diubah", pengiriman));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Pengiriman>> approvePengiriman(
            @PathVariable UUID id,
            @RequestBody ApprovePengirimanRequest request) {
        Pengiriman pengiriman = pengirimanService.setujuiPengiriman(id, request.getMandorId());
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman berhasil disetujui", pengiriman));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Pengiriman>> rejectPengiriman(
            @PathVariable UUID id,
            @RequestBody RejectPengirimanRequest request) {
        Pengiriman pengiriman = pengirimanService.tolakPengiriman(
                id,
                request.getMandorId(),
                request.getAlasanPenolakan());
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman berhasil ditolak", pengiriman));
    }

    @GetMapping("/supir/{supirTrukId}")
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getDaftarPengirimanSupir(
            @PathVariable UUID supirTrukId) {
        List<Pengiriman> pengirimanList = pengirimanService.getDaftarPengirimanSupir(supirTrukId);
        return ResponseEntity.ok(
                ApiResponse.success("Daftar pengiriman supir berhasil diambil", pengirimanList));
    }

    @GetMapping("/berlangsung")
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getDaftarPengirimanBerlangsung() {
        List<Pengiriman> pengirimanList = pengirimanService.getDaftarPengirimanBerlangsung();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar pengiriman berlangsung berhasil diambil", pengirimanList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pengiriman>> getPengirimanById(@PathVariable UUID id) {
        Pengiriman pengiriman = pengirimanService.getPengirimanById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Pengiriman berhasil diambil", pengiriman));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getAllPengiriman() {
        List<Pengiriman> pengirimanList = pengirimanService.getAllPengiriman();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar semua pengiriman berhasil diambil", pengirimanList));
    }
}
