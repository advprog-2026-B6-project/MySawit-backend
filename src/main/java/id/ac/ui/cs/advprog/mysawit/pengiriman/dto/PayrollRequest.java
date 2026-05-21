package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequest {
    private UUID pengirimanId;
    private UUID supirTrukId;
    private Long mandorId;
    private double muatanKg;
    private String tujuan;
    private LocalDateTime waktuDisetujui;
}