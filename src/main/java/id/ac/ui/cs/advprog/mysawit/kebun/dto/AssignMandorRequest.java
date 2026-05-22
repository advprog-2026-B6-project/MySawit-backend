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
public class AssignMandorRequest {

    @NotNull(message = "mandorId harus diisi")
    private Long mandorId;
}
