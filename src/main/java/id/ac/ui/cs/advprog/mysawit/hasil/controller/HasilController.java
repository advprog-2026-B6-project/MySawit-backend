package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilHistoryResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilTodayResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.mapper.HasilHistoryResponseMapper;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.security.HasilAccessPolicy;
import id.ac.ui.cs.advprog.mysawit.hasil.security.HasilCurrentUserService;
import id.ac.ui.cs.advprog.mysawit.hasil.service.HasilHistoryQueryService;
import id.ac.ui.cs.advprog.mysawit.hasil.service.HasilService;

@RestController
@RequestMapping("/hasil-reports")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class HasilController {
    private final HasilService hasilService;
    private final HasilHistoryQueryService historyQueryService;
    private final HasilCurrentUserService currentUserService;
    private final HasilAccessPolicy accessPolicy;
    private final HasilHistoryResponseMapper responseMapper;

    public HasilController(
            HasilService hasilService,
            HasilHistoryQueryService historyQueryService,
            HasilCurrentUserService currentUserService,
            HasilAccessPolicy accessPolicy,
            HasilHistoryResponseMapper responseMapper
    ) {
        this.hasilService = hasilService;
        this.historyQueryService = historyQueryService;
        this.currentUserService = currentUserService;
        this.accessPolicy = accessPolicy;
        this.responseMapper = responseMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam("kilogram") double kilogram,
            @RequestParam("news") String news,
            @RequestParam("photos") List<MultipartFile> photos
    ) {
        // TODO: upload photos using the storage module and store returned URLs.
        String workerId = currentUserService.currentBuruhUsername();

        List<String> photoUrls = photos.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();

        Hasil report = hasilService.create(workerId, kilogram, news, photoUrls);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", report.getId());
        payload.put("workerId", report.getWorkerId());
        payload.put("hasilDate", report.getHasilDate());
        payload.put("locked", report.isLocked());
        payload.put("status", report.getStatus().name());
        payload.put("message", "Laporan hasil panen tersimpan dan terkunci");
        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    @GetMapping("/me/today")
    public HasilTodayResponse todayStatus() {
        String workerId = currentUserService.currentBuruhUsername();
        boolean hasSubmitted = hasilService
                .findByWorkerAndDate(workerId, LocalDate.now())
                .isPresent();

        if (!hasSubmitted) {
            return new HasilTodayResponse(false, false, "Anda belum melaporkan panen hari ini");
        }

        return new HasilTodayResponse(true, true, "Panen hari ini sudah dilaporkan dan tidak bisa diedit");
    }

    @GetMapping("/me/history")
    public ResponseEntity<List<HasilHistoryResponse>> myHistory(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) HasilStatus status
    ) {
        String workerId = currentUserService.currentBuruhUsername();
        return ResponseEntity.ok(historyQueryService.personalHistory(workerId, startDate, endDate, status));
    }

    @GetMapping("/mandor/history")
    public ResponseEntity<List<HasilHistoryResponse>> mandorHistory(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String workerName
    ) {
        String mandorUsername = currentUserService.currentMandorUsername();
        return ResponseEntity.ok(historyQueryService.mandorHistory(mandorUsername, date, workerName));
    }

    @GetMapping("/mandor/workers/{workerId}/history")
    public ResponseEntity<List<HasilHistoryResponse>> workerHistoryForMandor(
            @PathVariable String workerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) HasilStatus status
    ) {
        String mandorUsername = currentUserService.currentMandorUsername();
        accessPolicy.ensureMandorSupervisesWorker(mandorUsername, workerId);
        return ResponseEntity.ok(historyQueryService.workerHistory(workerId, startDate, endDate, status));
    }

    @PutMapping("/mandor/{reportId}/approve")
    public ResponseEntity<HasilHistoryResponse> approveReport(@PathVariable String reportId) {
        String mandorUsername = currentUserService.currentMandorUsername();
        Hasil report = hasilService.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("hasil report not found"));
        accessPolicy.ensureMandorSupervisesWorker(mandorUsername, report.getWorkerId());
        return ResponseEntity.ok(responseMapper.toResponse(hasilService.approve(reportId)));
    }

    @PutMapping("/mandor/{reportId}/reject")
    public ResponseEntity<HasilHistoryResponse> rejectReport(
            @PathVariable String reportId,
            @RequestBody Map<String, String> request
    ) {
        String mandorUsername = currentUserService.currentMandorUsername();
        Hasil report = hasilService.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("hasil report not found"));
        accessPolicy.ensureMandorSupervisesWorker(mandorUsername, report.getWorkerId());

        String rejectionReason = request == null ? null : request.get("rejectionReason");
        return ResponseEntity.ok(responseMapper.toResponse(hasilService.reject(reportId, rejectionReason)));
    }

    @GetMapping("/pengiriman/available")
    public ResponseEntity<List<HasilHistoryResponse>> availableForPengiriman() {
        return ResponseEntity.ok(historyQueryService.availableForPengiriman());
    }
}



