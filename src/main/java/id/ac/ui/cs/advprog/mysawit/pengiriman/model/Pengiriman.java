package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pengiriman {
    public static final double MAX_MUATAN_KG = 400.0;

    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID supirTrukId;
    private UUID mandorId;
    private double muatanKg;
    private String tujuan;

    @Setter(lombok.AccessLevel.NONE)
    @Builder.Default
    private StatusPengiriman status = StatusPengiriman.MENUNGGU;

    @Builder.Default
    private LocalDateTime waktuDibuat = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime waktuDiperbarui = LocalDateTime.now();

    public void setStatus(StatusPengiriman status) {
        this.status = status;
        this.waktuDiperbarui = LocalDateTime.now();
    }

    public boolean isSedangBerlangsung() {
        return status == StatusPengiriman.MEMUAT || status == StatusPengiriman.MENGIRIM;
    }
}
