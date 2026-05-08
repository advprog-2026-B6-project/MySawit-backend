package id.ac.ui.cs.advprog.mysawit.pembayaran.service;

import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollResponse;

import java.time.LocalDate;
import java.util.List;

public interface PayrollService {
    PayrollResponse createPayroll(PayrollCreateRequest request);
    List<PayrollResponse> getPayrollsByUsernameForAdmin(String username, LocalDate startDate, LocalDate endDate);
    List<PayrollResponse> getPayrollsForWorker(String username, LocalDate startDate, LocalDate endDate, String status);
    
    // Method solely for wage calculation to allow for unit tests
    java.math.BigDecimal calculateWage(String role, java.math.BigDecimal totalKg);
}
