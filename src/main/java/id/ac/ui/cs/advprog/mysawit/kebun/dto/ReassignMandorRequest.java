package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReassignMandorRequest {

    @NotNull(message = "mandorId harus diisi")
    private Long mandorId;

    @NotBlank(message = "fromKebunId harus diisi")
    private String fromKebunId;

    @NotBlank(message = "toKebunId harus diisi")
    private String toKebunId;
}
