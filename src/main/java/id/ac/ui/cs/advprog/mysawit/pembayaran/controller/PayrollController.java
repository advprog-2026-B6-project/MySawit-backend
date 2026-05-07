package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pembayaran")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // --- Admin Endpoints ---
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/payroll")
    // @PreAuthorize("hasRole('ADMIN_UTAMA')")
    public ResponseEntity<PayrollResponse> generatePayroll(@RequestBody PayrollCreateRequest request) {
        return ResponseEntity.ok(payrollService.createPayroll(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/payroll/user/{username}")
    // @PreAuthorize("hasRole('ADMIN_UTAMA')")
    public ResponseEntity<List<PayrollResponse>> getPayrollForUserAsAdmin(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(payrollService.getPayrollsByUsernameForAdmin(username, startDate, endDate));
    }

    // --- Admin Endpoints ---
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/payroll/{id}/approve")
    public ResponseEntity<PayrollResponse> approvePayroll(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.approvePayroll(id));
    }

    @PostMapping("/admin/payroll/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PayrollResponse> rejectPayroll(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(payrollService.rejectPayroll(id, reason));
    }

    // --- Worker Endpoints ---
    @PreAuthorize("hasAnyAuthority('BURUH', 'SUPIR', 'MANDOR')")
    @GetMapping("/payroll/me")
    public ResponseEntity<List<PayrollResponse>> getMyPayrolls(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        return ResponseEntity.ok(payrollService.getPayrollsForWorker(currentUsername, startDate, endDate, status));
    }
}
