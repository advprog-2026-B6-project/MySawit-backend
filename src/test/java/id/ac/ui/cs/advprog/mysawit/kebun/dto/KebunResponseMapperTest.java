package id.ac.ui.cs.advprog.mysawit.kebun.dto;

import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KebunResponseMapperTest {

    private KebunResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new KebunResponseMapper();
    }

    private KebunSawit createDomainKebun() {
        return new KebunSawit(
                "id-1", "Kebun Test", "KB-0001", 4.0,
                new Coordinate(0, 200), new Coordinate(0, 0),
                new Coordinate(200, 200), new Coordinate(200, 0));
    }

    @Test
    void toDomain_fromCreateRequest_shouldMapAllFields() {
        CreateKebunRequest request = new CreateKebunRequest(
                "Kebun Baru", "KB-0001",
                new CoordinateRequest(0.0, 200.0),
                new CoordinateRequest(0.0, 0.0),
                new CoordinateRequest(200.0, 200.0),
                new CoordinateRequest(200.0, 0.0));

        KebunSawit domain = mapper.toDomain(request);

        assertEquals("Kebun Baru", domain.getNamaKebun());
        assertEquals("KB-0001", domain.getKodeUnik());
        assertEquals(0, domain.getKiriAtas().getX());
        assertEquals(200, domain.getKiriAtas().getY());
    }

    @Test
    void toDomain_fromUpdateRequest_shouldMapAllFieldsExceptKode() {
        UpdateKebunRequest request = new UpdateKebunRequest(
                "Kebun Updated",
                new CoordinateRequest(10.0, 210.0),
                new CoordinateRequest(10.0, 10.0),
                new CoordinateRequest(210.0, 210.0),
                new CoordinateRequest(210.0, 10.0));

        KebunSawit domain = mapper.toDomain(request);

        assertEquals("Kebun Updated", domain.getNamaKebun());
        assertNull(domain.getKodeUnik()); // Update doesn't set kodeUnik
        assertEquals(10, domain.getKiriAtas().getX());
    }

    @Test
    void toResponse_shouldMapAllFields() {
        KebunSawit kebun = createDomainKebun();
        KebunResponse response = mapper.toResponse(kebun);

        assertEquals("id-1", response.getId());
        assertEquals("Kebun Test", response.getNamaKebun());
        assertEquals("KB-0001", response.getKodeUnik());
        assertEquals(4.0, response.getLuasHektare());
        assertEquals(0, response.getKiriAtas().getX());
    }

    @Test
    void toResponses_shouldMapList() {
        KebunSawit k1 = createDomainKebun();
        KebunSawit k2 = new KebunSawit(
                "id-2", "Kebun 2", "KB-0002", 9.0,
                new Coordinate(500, 700), new Coordinate(500, 400),
                new Coordinate(800, 700), new Coordinate(800, 400));

        List<KebunResponse> responses = mapper.toResponses(List.of(k1, k2));

        assertEquals(2, responses.size());
        assertEquals("KB-0001", responses.get(0).getKodeUnik());
        assertEquals("KB-0002", responses.get(1).getKodeUnik());
    }

    @Test
    void toResponses_emptyList_shouldReturnEmpty() {
        List<KebunResponse> responses = mapper.toResponses(List.of());
        assertTrue(responses.isEmpty());
    }

    @Test
    void toDetailResponse_shouldMapAllFields() {
        KebunSawit kebun = createDomainKebun();
        MandorInfo mandor = new MandorInfo(10L, "Pak Mandor", "CERT-001");
        List<SupirInfo> supirs = List.of(new SupirInfo(20L, "Supir Andi"));

        KebunDetailResponse detail = mapper.toDetailResponse(kebun, mandor, supirs);

        assertEquals("id-1", detail.getId());
        assertEquals("Kebun Test", detail.getNamaKebun());
        assertNotNull(detail.getMandor());
        assertEquals("Pak Mandor", detail.getMandor().getFullname());
        assertEquals(1, detail.getSupirList().size());
    }

    @Test
    void toDetailResponse_nullMandor_shouldSetNull() {
        KebunSawit kebun = createDomainKebun();

        KebunDetailResponse detail = mapper.toDetailResponse(kebun, null, List.of());

        assertNull(detail.getMandor());
        assertTrue(detail.getSupirList().isEmpty());
    }
}
