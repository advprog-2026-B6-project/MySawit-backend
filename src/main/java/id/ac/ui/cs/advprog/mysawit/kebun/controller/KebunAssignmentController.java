package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.ReassignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.ReassignSupirRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/kebun")

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
        assignmentService.assignMandor(kebunId, request.getMandorId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Mandor berhasil ditugaskan ke kebun"));
    }

    @PutMapping("/mandor/reassign")
    public ResponseEntity<Object> reassignMandor(@Valid @RequestBody ReassignMandorRequest request) {
        assignmentService.reassignMandor(
                request.getMandorId(),
                request.getFromKebunId(),
                request.getToKebunId());
        return ResponseEntity.ok(Map.of("message", "Mandor berhasil dipindahkan ke kebun baru"));
    }

    // === SUPIR ASSIGNMENT ===

    @PostMapping("/{kebunId}/supir")
    public ResponseEntity<Object> assignSupir(
            @PathVariable String kebunId,
            @Valid @RequestBody AssignSupirRequest request) {
        assignmentService.assignSupir(kebunId, request.getSupirId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Supir Truk berhasil ditugaskan ke kebun"));
    }

    @PutMapping("/supir/reassign")
    public ResponseEntity<Object> reassignSupir(@Valid @RequestBody ReassignSupirRequest request) {
        assignmentService.reassignSupir(
                request.getSupirId(),
                request.getFromKebunId(),
                request.getToKebunId());
        return ResponseEntity.ok(Map.of("message", "Supir Truk berhasil dipindahkan ke kebun baru"));
    }
}
