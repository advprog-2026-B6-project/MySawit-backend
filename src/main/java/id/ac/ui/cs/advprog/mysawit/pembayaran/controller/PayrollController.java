package id.ac.ui.cs.advprog.mysawit.pembayaran.controller;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;
import id.ac.ui.cs.advprog.mysawit.pembayaran.service.PayrollService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // --- Admin Endpoints ---

    @PostMapping("/admin/payroll")
    // @PreAuthorize("hasRole('ADMIN_UTAMA')")
    public ResponseEntity<PayrollResponse> generatePayroll(@RequestBody PayrollCreateRequest request) {
        return ResponseEntity.ok(payrollService.createPayroll(request));
    }

    @GetMapping("/admin/payroll/user/{username}")
    // @PreAuthorize("hasRole('ADMIN_UTAMA')")
    public ResponseEntity<List<PayrollResponse>> getPayrollForUserAsAdmin(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(payrollService.getPayrollsByUsernameForAdmin(username, startDate, endDate));
    }

    // --- Worker Endpoints ---

    @GetMapping("/payroll/me")
    // @PreAuthorize("hasAnyRole('BURUH', 'SUPIR', 'MANDOR')")
    public ResponseEntity<List<PayrollResponse>> getMyPayrolls(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
            
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        return ResponseEntity.ok(payrollService.getPayrollsForWorker(currentUsername, startDate, endDate, status));
    }
}
