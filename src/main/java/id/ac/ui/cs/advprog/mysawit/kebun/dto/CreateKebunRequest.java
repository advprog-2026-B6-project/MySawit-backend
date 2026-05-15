package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
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
public class CreateKebunRequest {

    @NotBlank(message = "Nama kebun harus diisi")
    private String namaKebun;

    @NotBlank(message = "Kode unik harus diisi")
    private String kodeUnik;

    @NotNull(message = "Koordinat kiri atas harus diisi")
    private Coordinate kiriAtas;

    @NotNull(message = "Koordinat kiri bawah harus diisi")
    private Coordinate kiriBawah;

    @NotNull(message = "Koordinat kanan atas harus diisi")
    private Coordinate kananAtas;

    @NotNull(message = "Koordinat kanan bawah harus diisi")
    private Coordinate kananBawah;
}
