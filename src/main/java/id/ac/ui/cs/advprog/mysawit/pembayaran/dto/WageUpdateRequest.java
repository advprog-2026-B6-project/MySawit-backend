package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class WageUpdateRequest {
    private BigDecimal upahBuruhPerKg;
    private BigDecimal upahSupirPerKg;
    private BigDecimal upahMandorPerKg;
}