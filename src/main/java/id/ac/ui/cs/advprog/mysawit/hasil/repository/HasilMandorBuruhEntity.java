package id.ac.ui.cs.advprog.mysawit.hasil.repository;

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
@Table(name = "hasil_mandor_buruh", uniqueConstraints = {
        @UniqueConstraint(columnNames = "buruhId")
})
public class HasilMandorBuruhEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Long mandorId;

    @Column(nullable = false)
    private Long buruhId;
}
