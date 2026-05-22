package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateRequest {

    @NotNull(message = "Koordinat x harus diisi")
    private Double x;

    @NotNull(message = "Koordinat y harus diisi")
    private Double y;
}
