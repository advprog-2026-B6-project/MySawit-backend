package id.ac.ui.cs.advprog.mysawit.kebun.repository;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.stereotype.Component;

@Component
public class KebunSawitEntityMapper {

    public KebunSawitEntity toEntity(KebunSawit domain) {
        return new KebunSawitEntity(
                domain.getId(),
                domain.getNamaKebun(),
                domain.getKodeUnik(),
                domain.getLuasHektare(),
                toEntityCoord(domain.getKiriAtas()),
                toEntityCoord(domain.getKiriBawah()),
                toEntityCoord(domain.getKananAtas()),
                toEntityCoord(domain.getKananBawah())
        );
    }

    public KebunSawit toDomain(KebunSawitEntity entity) {
        return new KebunSawit(
                entity.getId(),
                entity.getNamaKebun(),
                entity.getKodeUnik(),
                entity.getLuasHektare(),
                toDomainCoord(entity.getKiriAtas()),
                toDomainCoord(entity.getKiriBawah()),
                toDomainCoord(entity.getKananAtas()),
                toDomainCoord(entity.getKananBawah())
        );
    }

    private CoordinateEmbeddable toEntityCoord(Coordinate domainCoord) {
        if (domainCoord == null) {
            return null;
        }
        return new CoordinateEmbeddable(domainCoord.getX(), domainCoord.getY());
    }

    private Coordinate toDomainCoord(CoordinateEmbeddable entityCoord) {
        if (entityCoord == null) {
            return null;
        }
        return new Coordinate(entityCoord.getX(), entityCoord.getY());
    }
}
