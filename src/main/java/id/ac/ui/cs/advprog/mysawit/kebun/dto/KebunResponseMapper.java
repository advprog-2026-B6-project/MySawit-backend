package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KebunResponseMapper {

    public KebunSawit toDomain(CreateKebunRequest request) {
        KebunSawit kebun = new KebunSawit();
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setKodeUnik(request.getKodeUnik());
        kebun.setKiriAtas(toCoordinate(request.getKiriAtas()));
        kebun.setKiriBawah(toCoordinate(request.getKiriBawah()));
        kebun.setKananAtas(toCoordinate(request.getKananAtas()));
        kebun.setKananBawah(toCoordinate(request.getKananBawah()));
        return kebun;
    }

    public KebunSawit toDomain(UpdateKebunRequest request) {
        KebunSawit kebun = new KebunSawit();
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setKiriAtas(toCoordinate(request.getKiriAtas()));
        kebun.setKiriBawah(toCoordinate(request.getKiriBawah()));
        kebun.setKananAtas(toCoordinate(request.getKananAtas()));
        kebun.setKananBawah(toCoordinate(request.getKananBawah()));
        return kebun;
    }

    public KebunResponse toResponse(KebunSawit kebun) {
        return new KebunResponse(
                kebun.getId(),
                kebun.getNamaKebun(),
                kebun.getKodeUnik(),
                kebun.getLuasHektare(),
                toCoordinateResponse(kebun.getKiriAtas()),
                toCoordinateResponse(kebun.getKiriBawah()),
                toCoordinateResponse(kebun.getKananAtas()),
                toCoordinateResponse(kebun.getKananBawah())
        );
    }

    public List<KebunResponse> toResponses(List<KebunSawit> kebunList) {
        return kebunList.stream()
                .map(this::toResponse)
                .toList();
    }

    public KebunDetailResponse toDetailResponse(
            KebunSawit kebun,
            MandorInfo mandorInfo,
            List<SupirInfo> supirList) {
        return new KebunDetailResponse(
                kebun.getId(),
                kebun.getNamaKebun(),
                kebun.getKodeUnik(),
                kebun.getLuasHektare(),
                kebun.getKiriAtas(),
                kebun.getKiriBawah(),
                kebun.getKananAtas(),
                kebun.getKananBawah(),
                mandorInfo,
                supirList
        );
    }

    private Coordinate toCoordinate(CoordinateRequest request) {
        return new Coordinate(request.getX(), request.getY());
    }

    private CoordinateResponse toCoordinateResponse(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        return new CoordinateResponse(coordinate.getX(), coordinate.getY());
    }
}
