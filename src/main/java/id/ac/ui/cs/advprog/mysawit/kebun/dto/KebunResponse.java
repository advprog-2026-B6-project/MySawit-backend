package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
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
    private Coordinate kiriAtas;
    private Coordinate kiriBawah;
    private Coordinate kananAtas;
    private Coordinate kananBawah;
}
