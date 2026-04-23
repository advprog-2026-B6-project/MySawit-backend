package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilTodayResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.service.HasilService;
import id.ac.ui.cs.advprog.mysawit.hasil.dto.HasilHistoryResponse;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;

@RestController
@RequestMapping("/hasil-reports")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class HasilController {
    private static final String BURUH_ROLE = "ROLE_BURUH";
    private static final String MANDOR_ROLE = "ROLE_MANDOR";

    private final HasilService hasilService;
    private final UserRepository userRepository;

    public HasilController(HasilService hasilService, UserRepository userRepository) {
        this.hasilService = hasilService;
        this.userRepository = userRepository;
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
        payload.put("status", report.getStatus().name());
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

    @GetMapping("/me/history")
    public ResponseEntity<List<HasilHistoryResponse>> myHistory(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) HasilStatus status
    ) {
        String workerId = getCurrentUsernameForRole(BURUH_ROLE);
        validateDateRange(startDate, endDate);

        List<HasilHistoryResponse> history = hasilService.findAll().stream()
                .filter(report -> workerId.equals(report.getWorkerId()))
                .filter(report -> matchesDateRange(report, startDate, endDate))
                .filter(report -> status == null || status.equals(report.getStatus()))
                .sorted(historyComparator())
                .map(this::toHistoryResponse)
                .toList();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/mandor/history")
    public ResponseEntity<List<HasilHistoryResponse>> mandorHistory(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String workerName
    ) {
        String mandorUsername = getCurrentUsernameForRole(MANDOR_ROLE);
        Set<String> supervisedWorkerIds = getSupervisedWorkerIds(mandorUsername);

        List<HasilHistoryResponse> history = hasilService.findAll().stream()
                .filter(report -> supervisedWorkerIds.contains(report.getWorkerId()))
                .filter(report -> date == null || date.equals(report.getHasilDate()))
                .filter(report -> matchesWorkerName(report.getWorkerId(), workerName))
                .sorted(historyComparator())
                .map(this::toHistoryResponse)
                .toList();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/mandor/workers/{workerId}/history")
    public ResponseEntity<List<HasilHistoryResponse>> workerHistoryForMandor(
            @PathVariable String workerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) HasilStatus status
    ) {
        String mandorUsername = getCurrentUsernameForRole(MANDOR_ROLE);
        validateDateRange(startDate, endDate);
        ensureWorkerBelongsToMandor(mandorUsername, workerId);

        List<HasilHistoryResponse> history = hasilService.findAll().stream()
                .filter(report -> workerId.equals(report.getWorkerId()))
                .filter(report -> matchesDateRange(report, startDate, endDate))
                .filter(report -> status == null || status.equals(report.getStatus()))
                .sorted(historyComparator())
                .map(this::toHistoryResponse)
                .toList();
        return ResponseEntity.ok(history);
    }

    private String getCurrentBuruhUsername() {
        return getCurrentUsernameForRole(BURUH_ROLE);
    }

    private String getCurrentUsernameForRole(String requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        boolean hasRequiredRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> requiredRole.equals(authority.getAuthority()));
        if (!hasRequiredRole) {
            throw new AccessDeniedException("Forbidden");
        }

        return authentication.getName();
    }

    private Set<String> getSupervisedWorkerIds(String mandorUsername) {
        return userRepository.findAll().stream()
                .filter(user -> mandorUsername.equals(user.getMandorUsername()))
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }

    private void ensureWorkerBelongsToMandor(String mandorUsername, String workerId) {
        boolean belongsToMandor = userRepository.findByUsername(workerId)
                .map(user -> mandorUsername.equals(user.getMandorUsername()))
                .orElse(false);

        if (!belongsToMandor) {
            throw new AccessDeniedException("Worker is not managed by this mandor");
        }
    }

    private boolean matchesWorkerName(String workerId, String workerName) {
        return workerName == null || workerName.isBlank()
                || userRepository.findByUsername(workerId)
                .map(User::getFullname)
                .map(fullname -> fullname != null && fullname.toLowerCase().contains(workerName.toLowerCase()))
                .orElse(false);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }
    }

    private boolean matchesDateRange(Hasil report, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && report.getHasilDate().isBefore(startDate)) {
            return false;
        }
        if (endDate != null && report.getHasilDate().isAfter(endDate)) {
            return false;
        }
        return true;
    }

    private Comparator<Hasil> historyComparator() {
        return Comparator.comparing(Hasil::getHasilDate).reversed().thenComparing(Hasil::getId);
    }

    private HasilHistoryResponse toHistoryResponse(Hasil report) {
        User worker = userRepository.findByUsername(report.getWorkerId()).orElse(null);
        String workerName = worker != null && worker.getFullname() != null && !worker.getFullname().isBlank()
                ? worker.getFullname()
                : report.getWorkerId();

        return new HasilHistoryResponse(
                report.getId(),
                report.getWorkerId(),
                workerName,
                report.getHasilDate(),
                report.getWeightKg(),
                report.getNews(),
                report.getStatus().name(),
                report.isLocked(),
                report.getPhotoUrls()
        );
    }
}



