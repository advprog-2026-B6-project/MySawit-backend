package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KebunResponse {
    private String id;
    private String namaKebun;
    private String kodeUnik;
    private Double luasHektare;
    private CoordinateResponse kiriAtas;
    private CoordinateResponse kiriBawah;
    private CoordinateResponse kananAtas;
    private CoordinateResponse kananBawah;
}
