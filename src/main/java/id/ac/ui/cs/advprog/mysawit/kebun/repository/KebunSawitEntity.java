package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kebun_sawit")
public class KebunSawitEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String namaKebun;

    @Column(nullable = false, unique = true)
    private String kodeUnik;

    @Column(nullable = false)
    private Double luasHektare;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "kiri_atas_x")),
        @AttributeOverride(name = "y", column = @Column(name = "kiri_atas_y"))
    })
    private CoordinateEmbeddable kiriAtas;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "kiri_bawah_x")),
        @AttributeOverride(name = "y", column = @Column(name = "kiri_bawah_y"))
    })
    private CoordinateEmbeddable kiriBawah;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "kanan_atas_x")),
        @AttributeOverride(name = "y", column = @Column(name = "kanan_atas_y"))
    })
    private CoordinateEmbeddable kananAtas;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "kanan_bawah_x")),
        @AttributeOverride(name = "y", column = @Column(name = "kanan_bawah_y"))
    })
    private CoordinateEmbeddable kananBawah;
}