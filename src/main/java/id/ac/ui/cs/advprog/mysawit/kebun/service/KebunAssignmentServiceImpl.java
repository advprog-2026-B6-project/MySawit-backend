package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KebunAssignmentServiceImpl implements KebunAssignmentService {

    private final KebunSawitRepository kebunRepository;
    private final KebunAssignmentRepository assignmentRepository;
    private final KebunUserReader userReader;

    public KebunAssignmentServiceImpl(KebunSawitRepository kebunRepository,
                                      KebunAssignmentRepository assignmentRepository,
                                      KebunUserReader userReader) {
        this.kebunRepository = kebunRepository;
        this.assignmentRepository = assignmentRepository;
        this.userReader = userReader;
    }

    @Override
    @Transactional
    public void assignMandor(String kebunId, Long mandorId) {
        // Validate kebun exists
        kebunRepository.findById(kebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun tidak ditemukan dengan id: " + kebunId));

        // Validate user exists and is a MANDOR
        UserSnapshot user = userReader.findUserById(mandorId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "User tidak ditemukan dengan id: " + mandorId));

        if (!"MANDOR".equals(user.getRole())) {
            throw new KebunConflictException(
                    "User dengan id " + mandorId + " bukan Mandor (role: " + user.getRole() + ")");
        }

        // Check kebun doesn't already have a mandor
        if (assignmentRepository.kebunHasMandor(kebunId)) {
            throw new KebunConflictException(
                    "Kebun sudah memiliki Mandor yang ditugaskan");
        }

        // Check mandor isn't already assigned to another kebun
        if (assignmentRepository.mandorIsAssigned(mandorId)) {
            throw new KebunConflictException(
                    "Mandor sudah ditugaskan ke kebun lain");
        }

        assignmentRepository.assignMandor(kebunId, mandorId);
    }

    @Override
    @Transactional
    public void reassignMandor(Long mandorId, String fromKebunId, String toKebunId) {
        // Validate both kebuns exist
        kebunRepository.findById(fromKebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun asal tidak ditemukan dengan id: " + fromKebunId));
        kebunRepository.findById(toKebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun tujuan tidak ditemukan dengan id: " + toKebunId));

        // Validate mandor is currently at fromKebun
        String currentKebunId = assignmentRepository.findKebunIdByMandorId(mandorId)
                .orElseThrow(() -> new KebunConflictException(
                        "Mandor belum ditugaskan ke kebun manapun"));

        if (!currentKebunId.equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Mandor tidak ditugaskan di kebun asal yang disebutkan");
        }

        // Validate toKebun doesn't already have a mandor
        if (assignmentRepository.kebunHasMandor(toKebunId)) {
            throw new KebunConflictException(
                    "Kebun tujuan sudah memiliki Mandor yang ditugaskan");
        }

        assignmentRepository.moveMandor(mandorId, fromKebunId, toKebunId);
    }

    @Override
    @Transactional
    public void assignSupir(String kebunId, Long supirId) {
        // Validate kebun exists
        kebunRepository.findById(kebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun tidak ditemukan dengan id: " + kebunId));

        // Validate user exists and is a SUPIR
        UserSnapshot user = userReader.findUserById(supirId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "User tidak ditemukan dengan id: " + supirId));

        if (!"SUPIR".equals(user.getRole())) {
            throw new KebunConflictException(
                    "User dengan id " + supirId + " bukan Supir Truk (role: " + user.getRole() + ")");
        }

        // Check supir isn't already assigned somewhere
        if (assignmentRepository.supirIsAssigned(supirId)) {
            throw new KebunConflictException(
                    "Supir Truk sudah ditugaskan ke kebun lain");
        }

        assignmentRepository.assignSupir(kebunId, supirId);
    }

    @Override
    @Transactional
    public void reassignSupir(Long supirId, String fromKebunId, String toKebunId) {
        // Validate both kebuns exist
        kebunRepository.findById(fromKebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun asal tidak ditemukan dengan id: " + fromKebunId));
        kebunRepository.findById(toKebunId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "Kebun tujuan tidak ditemukan dengan id: " + toKebunId));

        // Validate supir is currently at fromKebun
        String currentKebunId = assignmentRepository.findKebunIdBySupirId(supirId)
                .orElseThrow(() -> new KebunConflictException(
                        "Supir Truk belum ditugaskan ke kebun manapun"));

        if (!currentKebunId.equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Supir Truk tidak ditugaskan di kebun asal yang disebutkan");
        }

        assignmentRepository.moveSupir(supirId, fromKebunId, toKebunId);
    }
}
