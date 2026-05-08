package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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
}
