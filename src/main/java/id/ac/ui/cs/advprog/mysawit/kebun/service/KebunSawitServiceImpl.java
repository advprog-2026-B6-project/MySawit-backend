package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KebunSawitServiceImpl implements KebunSawitService {

    private final KebunSawitRepository repository;
    private final KebunAssignmentRepository assignmentRepository;
    private final KebunGeometry geometry;
    private final KebunValidator validator;
    private final KebunSearchService searchService;
    private final KebunDetailAssembler detailAssembler;

    public KebunSawitServiceImpl(KebunSawitRepository repository,
                                 KebunAssignmentRepository assignmentRepository,
                                 KebunGeometry geometry,
                                 KebunValidator validator,
                                 KebunSearchService searchService,
                                 KebunDetailAssembler detailAssembler) {
        this.repository = repository;
        this.assignmentRepository = assignmentRepository;
        this.geometry = geometry;
        this.validator = validator;
        this.searchService = searchService;
        this.detailAssembler = detailAssembler;
    }

    @Override
    public KebunSawit create(KebunSawit kebun) {
        validator.validateCreate(kebun);
        kebun.setLuasHektare(geometry.calculateHectares(kebun));
        kebun.setId(UUID.randomUUID().toString());
        return repository.save(kebun);
    }

    @Override
    public List<KebunSawit> findAll(String searchNama, String searchKode) {
        return searchService.findAll(searchNama, searchKode);
    }

    @Override
    public Optional<KebunSawit> findByKodeUnik(String kodeUnik) {
        return repository.findByKodeUnik(kodeUnik);
    }

    @Override
    public KebunSawit update(String id, KebunSawit updatedKebun) {
        KebunSawit existing = repository.findById(id)
                .orElseThrow(() -> new KebunNotFoundException("Kebun tidak ditemukan dengan id: " + id));

        validator.validateUpdate(id, updatedKebun);
        double luasHektare = geometry.calculateHectares(updatedKebun);

        // Lock kodeUnik: always keep original
        existing.setNamaKebun(updatedKebun.getNamaKebun());
        existing.setLuasHektare(luasHektare);
        existing.setKiriAtas(updatedKebun.getKiriAtas());
        existing.setKiriBawah(updatedKebun.getKiriBawah());
        existing.setKananAtas(updatedKebun.getKananAtas());
        existing.setKananBawah(updatedKebun.getKananBawah());

        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        repository.findById(id)
                .orElseThrow(() -> new KebunNotFoundException("Kebun tidak ditemukan dengan id: " + id));

        // Cek apakah kebun masih memiliki Mandor yang ditugaskan
        if (assignmentRepository.kebunHasMandor(id)) {
            throw new KebunConflictException(
                    "Tidak dapat menghapus kebun yang masih memiliki Mandor yang ditugaskan");
        }

        repository.deleteById(id);
    }

    @Override
    public KebunDetailResponse getDetail(String kebunId, String searchSupirNama) {
        return detailAssembler.getDetail(kebunId, searchSupirNama);
    }
}
