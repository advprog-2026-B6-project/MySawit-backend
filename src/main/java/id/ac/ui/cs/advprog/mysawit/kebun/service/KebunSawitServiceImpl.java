package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.MandorInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.SupirInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KebunSawitServiceImpl implements KebunSawitService {

    private final KebunSawitRepository repository;
    private final KebunMandorJpaRepository kebunMandorRepository;
    private final KebunSupirJpaRepository kebunSupirRepository;
    private final KebunUserReader userReader;
    private final KebunGeometry geometry;
    private final KebunValidator validator;

    public KebunSawitServiceImpl(KebunSawitRepository repository,
                                 KebunMandorJpaRepository kebunMandorRepository,
                                 KebunSupirJpaRepository kebunSupirRepository,
                                 KebunUserReader userReader,
                                 KebunGeometry geometry,
                                 KebunValidator validator) {
        this.repository = repository;
        this.kebunMandorRepository = kebunMandorRepository;
        this.kebunSupirRepository = kebunSupirRepository;
        this.userReader = userReader;
        this.geometry = geometry;
        this.validator = validator;
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
        return repository.findAll().stream()
                .filter(k -> searchNama == null || searchNama.isEmpty()
                        || k.getNamaKebun().toLowerCase().contains(searchNama.toLowerCase()))
                .filter(k -> searchKode == null || searchKode.isEmpty()
                        || k.getKodeUnik().toLowerCase().contains(searchKode.toLowerCase()))
                .toList();
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
        if (kebunMandorRepository.existsByKebunId(id)) {
            throw new KebunConflictException(
                    "Tidak dapat menghapus kebun yang masih memiliki Mandor yang ditugaskan");
        }

        repository.deleteById(id);
    }

    @Override
    public KebunDetailResponse getDetail(String kebunId, String searchSupirNama) {
        KebunSawit kebun = repository.findById(kebunId)
                .orElseThrow(() -> new KebunNotFoundException("Kebun tidak ditemukan dengan id: " + kebunId));

        // Resolve Mandor info
        MandorInfo mandorInfo = kebunMandorRepository.findByKebunId(kebunId)
                .flatMap(assignment -> userReader.findUserById(assignment.getMandorId()))
                .map(snapshot -> new MandorInfo(
                        snapshot.getId(),
                        snapshot.getFullname(),
                        snapshot.getCertificationNumber()))
                .orElse(null);

        // Resolve Supir list
        List<KebunSupirEntity> supirAssignments = kebunSupirRepository.findAllByKebunId(kebunId);
        List<Long> supirIds = supirAssignments.stream()
                .map(KebunSupirEntity::getSupirId)
                .collect(Collectors.toList());

        List<SupirInfo> supirList;
        if (supirIds.isEmpty()) {
            supirList = new ArrayList<>();
        } else {
            supirList = userReader.findUsersByIds(supirIds).stream()
                    .map(snapshot -> new SupirInfo(snapshot.getId(), snapshot.getFullname()))
                    .collect(Collectors.toList());
        }

        // Apply search filter on supir names
        if (searchSupirNama != null && !searchSupirNama.isEmpty()) {
            String lowerSearch = searchSupirNama.toLowerCase();
            supirList = supirList.stream()
                    .filter(s -> s.getFullname() != null
                            && s.getFullname().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

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
}
