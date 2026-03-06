package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.SupirTrukService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supir-truk")
public class SupirTrukController {

    private final SupirTrukService supirTrukService;

    public SupirTrukController(SupirTrukService supirTrukService) {
        this.supirTrukService = supirTrukService;
    }

    @GetMapping("/bertugas")
    public ResponseEntity<ApiResponse<List<SupirTruk>>> getDaftarSupirBertugas() {
        List<SupirTruk> supirList = supirTrukService.getDaftarSupirBertugas();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar supir bertugas berhasil diambil", supirList));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupirTruk>>> getAllSupirTruk() {
        List<SupirTruk> supirList = supirTrukService.getAllSupirTruk();
        return ResponseEntity.ok(
                ApiResponse.success("Daftar semua supir truk berhasil diambil", supirList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupirTruk>> getSupirTrukById(@PathVariable UUID id) {
        try {
            SupirTruk supirTruk = supirTrukService.getSupirTrukById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Supir truk berhasil diambil", supirTruk));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupirTruk>> tambahSupirTruk(@RequestBody SupirTruk supirTruk) {
        SupirTruk savedSupir = supirTrukService.tambahSupirTruk(supirTruk);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supir truk berhasil ditambahkan", savedSupir));
    }
}
