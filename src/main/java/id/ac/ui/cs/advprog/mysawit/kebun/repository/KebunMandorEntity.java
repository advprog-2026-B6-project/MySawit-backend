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
@Table(name = "kebun_mandor", uniqueConstraints = {
    @UniqueConstraint(columnNames = "kebunId"),
    @UniqueConstraint(columnNames = "mandorId")
})
public class KebunMandorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String kebunId;

    @Column(nullable = false)
    private Long mandorId;
}
