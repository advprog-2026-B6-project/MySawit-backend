package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilTodayResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.service.HasilService;

@RestController
@RequestMapping("/hasil-reports")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class HasilController {
    private static final String BURUH_ROLE = "ROLE_BURUH";

    private final HasilService hasilService;

    public HasilController(HasilService hasilService) {
        this.hasilService = hasilService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam("kilogram") double kilogram,
            @RequestParam("news") String news,
            @RequestParam("photos") List<MultipartFile> photos
    ) {
        // TODO: upload photos using the storage module and store returned URLs.
        String workerId = getCurrentBuruhUsername();

        List<String> photoUrls = photos.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();

        Hasil report = hasilService.create(workerId, kilogram, news, photoUrls);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", report.getId());
        payload.put("workerId", report.getWorkerId());
        payload.put("hasilDate", report.getHasilDate());
        payload.put("locked", report.isLocked());
        payload.put("message", "Laporan hasil panen tersimpan dan terkunci");
        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    @GetMapping("/me/today")
    public HasilTodayResponse todayStatus() {
        String workerId = getCurrentBuruhUsername();
        boolean hasSubmitted = hasilService
                .findByWorkerAndDate(workerId, LocalDate.now())
                .isPresent();

        if (!hasSubmitted) {
            return new HasilTodayResponse(false, false, "Anda belum melaporkan panen hari ini");
        }

        return new HasilTodayResponse(true, true, "Panen hari ini sudah dilaporkan dan tidak bisa diedit");
    }

    private String getCurrentBuruhUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        boolean isBuruh = authentication.getAuthorities().stream()
                .anyMatch(authority -> BURUH_ROLE.equals(authority.getAuthority()));
        if (!isBuruh) {
            throw new AccessDeniedException("Only buruh can access hasil submission");
        }

        return authentication.getName();
    }
}



