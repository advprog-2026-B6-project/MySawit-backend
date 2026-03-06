package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.WageUpdateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.WageSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wages") //tentatif dapat diubah juga sesuai diskusi kelompok
@PreAuthorize("hasRole('ADMIN_UTAMA')") // mungkin nanti diubah mengikuti modul user
public class WageSettingController {

    private final WageSettingService wageSettingService;

    public WageSettingController(WageSettingService wageSettingService) {
        this.wageSettingService = wageSettingService;
    }

    @GetMapping
    public ResponseEntity<WageSetting> getWages() {
        return ResponseEntity.ok(wageSettingService.getWageSetting());
    }

    @PutMapping
    public ResponseEntity<WageSetting> updateWages(@RequestBody WageUpdateRequest request) {
        WageSetting updatedSetting = wageSettingService.updateWages(
                request.getUpahBuruhPerKg(),
                request.getUpahSupirPerKg(),
                request.getUpahMandorPerKg()
        );
        return ResponseEntity.ok(updatedSetting);
    }
}