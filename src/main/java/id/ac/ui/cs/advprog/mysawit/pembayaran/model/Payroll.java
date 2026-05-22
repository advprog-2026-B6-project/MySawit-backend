package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalKg;

    @Column(nullable = false)
    private BigDecimal totalWage; // Will store the 90% of calculated wage

    @Column(nullable = false)
    private String status; // e.g., "PENDING", "PAID"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    public id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.PayrollState getPayrollState() {
        if ("ACCEPTED".equals(status)) {
            return new id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.AcceptedState();
        } else if ("REJECTED".equals(status)) {
            return new id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.RejectedState();
        }
        return new id.ac.ui.cs.advprog.mysawit.pembayaran.model.state.PendingState();
    }

    public void approve() {
        getPayrollState().approve(this);
    }

    public void reject(String reason) {
        getPayrollState().reject(this, reason);
    }
}
