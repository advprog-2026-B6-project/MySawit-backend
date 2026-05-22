package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunResponseMapper;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.MandorInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.SupirInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KebunDetailAssembler {

    private final KebunSawitRepository repository;
    private final KebunAssignmentRepository assignmentRepository;
    private final KebunUserReader userReader;
    private final KebunResponseMapper mapper;

    public KebunDetailAssembler(KebunSawitRepository repository,
                                KebunAssignmentRepository assignmentRepository,
                                KebunUserReader userReader,
                                KebunResponseMapper mapper) {
        this.repository = repository;
        this.assignmentRepository = assignmentRepository;
        this.userReader = userReader;
        this.mapper = mapper;
    }

    public KebunDetailResponse getDetail(String kebunId, String searchSupirNama) {
        KebunSawit kebun = repository.findById(kebunId)
                .orElseThrow(() -> new KebunNotFoundException("Kebun tidak ditemukan dengan id: " + kebunId));

        MandorInfo mandorInfo = resolveMandor(kebunId);
        List<SupirInfo> supirList = resolveSupirs(kebunId, searchSupirNama);

        return mapper.toDetailResponse(kebun, mandorInfo, supirList);
    }

    private MandorInfo resolveMandor(String kebunId) {
        return assignmentRepository.findMandorIdByKebunId(kebunId)
                .flatMap(userReader::findUserById)
                .map(snapshot -> new MandorInfo(
                        snapshot.getId(),
                        snapshot.getFullname(),
                        snapshot.getCertificationNumber()))
                .orElse(null);
    }

    private List<SupirInfo> resolveSupirs(String kebunId, String searchSupirNama) {
        List<Long> supirIds = assignmentRepository.findSupirIdsByKebunId(kebunId);
        if (supirIds.isEmpty()) {
            return List.of();
        }

        List<SupirInfo> supirList = userReader.findUsersByIds(supirIds).stream()
                .map(snapshot -> new SupirInfo(snapshot.getId(), snapshot.getFullname()))
                .toList();

        if (searchSupirNama == null || searchSupirNama.isEmpty()) {
            return supirList;
        }

        String lowerSearch = searchSupirNama.toLowerCase();
        return supirList.stream()
                .filter(s -> s.getFullname() != null
                        && s.getFullname().toLowerCase().contains(lowerSearch))
                .toList();
    }
}
