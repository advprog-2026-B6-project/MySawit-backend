package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollResponse {
    private Long id;
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalKg;
    private BigDecimal totalWage;
    private String status;
    private LocalDateTime createdAt;
}
