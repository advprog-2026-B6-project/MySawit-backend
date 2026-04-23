package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestBody Map<String, Long> body) {
        try {
            Long mandorId = body.get("mandorId");
            if (mandorId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "mandorId harus diisi"));
            }
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
    public ResponseEntity<Object> reassignMandor(@RequestBody Map<String, Object> body) {
        try {
            Long mandorId = ((Number) body.get("mandorId")).longValue();
            String fromKebunId = (String) body.get("fromKebunId");
            String toKebunId = (String) body.get("toKebunId");

            if (mandorId == null || fromKebunId == null || toKebunId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "mandorId, fromKebunId, dan toKebunId harus diisi"));
            }

            assignmentService.reassignMandor(mandorId, fromKebunId, toKebunId);
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
            @RequestBody Map<String, Long> body) {
        try {
            Long supirId = body.get("supirId");
            if (supirId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "supirId harus diisi"));
            }
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
    public ResponseEntity<Object> reassignSupir(@RequestBody Map<String, Object> body) {
        try {
            Long supirId = ((Number) body.get("supirId")).longValue();
            String fromKebunId = (String) body.get("fromKebunId");
            String toKebunId = (String) body.get("toKebunId");

            if (supirId == null || fromKebunId == null || toKebunId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "supirId, fromKebunId, dan toKebunId harus diisi"));
            }

            assignmentService.reassignSupir(supirId, fromKebunId, toKebunId);
            return ResponseEntity.ok(Map.of("message", "Supir Truk berhasil dipindahkan ke kebun baru"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}
