package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.ReassignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.ReassignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/kebun")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class KebunAssignmentController {

    private final KebunAssignmentService assignmentService;

    public KebunAssignmentController(KebunAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    // === MANDOR ASSIGNMENT ===

    @PostMapping("/{kebunId}/mandor")
    public ResponseEntity<Object> assignMandor(
            @PathVariable String kebunId,
            @Valid @RequestBody AssignMandorRequest request) {
        try {
            Long mandorId = request.getMandorId();
            assignmentService.assignMandor(kebunId, mandorId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Mandor berhasil ditugaskan ke kebun"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/mandor/reassign")
    public ResponseEntity<Object> reassignMandor(@Valid @RequestBody ReassignMandorRequest request) {
        try {
            assignmentService.reassignMandor(
                    request.getMandorId(),
                    request.getFromKebunId(),
                    request.getToKebunId());
            return ResponseEntity.ok(Map.of("message", "Mandor berhasil dipindahkan ke kebun baru"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // === SUPIR ASSIGNMENT ===

    @PostMapping("/{kebunId}/supir")
    public ResponseEntity<Object> assignSupir(
            @PathVariable String kebunId,
            @Valid @RequestBody AssignSupirRequest request) {
        try {
            Long supirId = request.getSupirId();
            assignmentService.assignSupir(kebunId, supirId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Supir Truk berhasil ditugaskan ke kebun"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/supir/reassign")
    public ResponseEntity<Object> reassignSupir(@Valid @RequestBody ReassignSupirRequest request) {
        try {
            assignmentService.reassignSupir(
                    request.getSupirId(),
                    request.getFromKebunId(),
                    request.getToKebunId());
            return ResponseEntity.ok(Map.of("message", "Supir Truk berhasil dipindahkan ke kebun baru"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Request tidak valid");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(Map.of("error", "Request tidak valid"));
    }
}
