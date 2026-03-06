package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CoordinateEmbeddable {
    private Double x;
    private Double y;
}