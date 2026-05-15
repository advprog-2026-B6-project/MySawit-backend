package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import jakarta.validation.Valid;
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
public class UpdateKebunRequest {

    @NotBlank(message = "Nama kebun harus diisi")
    private String namaKebun;

    @Valid
    @NotNull(message = "Koordinat kiri atas harus diisi")
    private CoordinateRequest kiriAtas;

    @Valid
    @NotNull(message = "Koordinat kiri bawah harus diisi")
    private CoordinateRequest kiriBawah;

    @Valid
    @NotNull(message = "Koordinat kanan atas harus diisi")
    private CoordinateRequest kananAtas;

    @Valid
    @NotNull(message = "Koordinat kanan bawah harus diisi")
    private CoordinateRequest kananBawah;
}
