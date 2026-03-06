package id.ac.ui.cs.advprog.mysawit.pembayaran.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wage_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WageSetting {

    @Id
    private String id = "DEFAULT";

    private BigDecimal upahBuruhPerKg;
    private BigDecimal upahSupirPerKg;
    private BigDecimal upahMandorPerKg;
}