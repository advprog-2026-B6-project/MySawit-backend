package id.ac.ui.cs.advprog.mysawit.kebun.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KebunSawit {
    private String id;
    private String namaKebun;
    private String kodeUnik;
    private Double luasHektare;

    private Coordinate kiriAtas;
    private Coordinate kiriBawah;
    private Coordinate kananAtas;
    private Coordinate kananBawah;

    public List<Coordinate> getKoordinatAsList() {
        return List.of(kiriAtas, kiriBawah, kananAtas, kananBawah);
    }
}