package id.ac.ui.cs.advprog.mysawit.pengiriman.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.PengirimanAssignmentService;

@RestController
@RequestMapping("/api/pengiriman/assignments")
public class PengirimanAssignmentController {

    private final PengirimanAssignmentService assignmentService;

    public PengirimanAssignmentController(PengirimanAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PengirimanAssignmentResponse>> createAssignment(
            @RequestBody PengirimanAssignmentRequest request) {
        try {
            PengirimanAssignmentResponse response = assignmentService.createAssignment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Penugasan pengiriman berhasil dibuat", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PengirimanAssignmentResponse>>> getAllAssignments() {
        List<PengirimanAssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(ApiResponse.success("Daftar penugasan pengiriman berhasil diambil", assignments));
    }
}
