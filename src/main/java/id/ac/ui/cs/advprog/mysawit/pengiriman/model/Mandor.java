package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mandor {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String nama;
    private String email;
}
