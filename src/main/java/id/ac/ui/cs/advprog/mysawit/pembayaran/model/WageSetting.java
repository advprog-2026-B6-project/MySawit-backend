package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "wage_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WageSetting {

    @Id
    @Builder.Default
    private String id = "DEFAULT";

    private BigDecimal upahBuruhPerKg;
    private BigDecimal upahSupirPerKg;
    private BigDecimal upahMandorPerKg;
}