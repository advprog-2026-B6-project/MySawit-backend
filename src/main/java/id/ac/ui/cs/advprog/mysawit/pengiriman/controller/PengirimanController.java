package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.BuatPengirimanRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.UbahStatusRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;

@RestController
@RequestMapping("/api/pengiriman")
public class PengirimanController {

    private final PengirimanService pengirimanService;

    public PengirimanController(PengirimanService pengirimanService) {
        this.pengirimanService = pengirimanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pengiriman>> buatPengiriman(
            @RequestBody BuatPengirimanRequest request) {
        try {
            // Auto-generate mandorId if not provided
            UUID mandorId = request.getMandorId();
            if (mandorId == null) {
                mandorId = UUID.randomUUID();
            }
            
            Pengiriman pengiriman = pengirimanService.buatPengiriman(
                    mandorId,
                    request.getSupirTrukId(),
                    request.getMuatanKg(),
                    request.getTujuan()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pengiriman berhasil dibuat", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Pengiriman>> ubahStatusPengiriman(
            @PathVariable UUID id,
            @RequestBody UbahStatusRequest request) {
        try {
            Pengiriman pengiriman = pengirimanService.ubahStatusPengiriman(
                    id,
                    request.getSupirTrukId(),
                    request.getStatusBaru()
            );
            return ResponseEntity.ok(
                    ApiResponse.success("Status pengiriman berhasil diubah", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/supir/{supirTrukId}")
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getDaftarPengirimanSupir(
            @PathVariable UUID supirTrukId) {
        try {
            List<Pengiriman> pengirimanList = pengirimanService.getDaftarPengirimanSupir(supirTrukId);
            return ResponseEntity.ok(
                    ApiResponse.success("Daftar pengiriman supir berhasil diambil", pengirimanList));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/berlangsung")
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getDaftarPengirimanBerlangsung() {
        List<Pengiriman> pengirimanList = pengirimanService.getDaftarPengirimanBerlangsung();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar pengiriman berlangsung berhasil diambil", pengirimanList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pengiriman>> getPengirimanById(@PathVariable UUID id) {
        try {
            Pengiriman pengiriman = pengirimanService.getPengirimanById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Pengiriman berhasil diambil", pengiriman));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Pengiriman>>> getAllPengiriman() {
        List<Pengiriman> pengirimanList = pengirimanService.getAllPengiriman();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar semua pengiriman berhasil diambil", pengirimanList));
    }
}
