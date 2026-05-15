package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
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
        requireKebunExists(kebunId, "Kebun tidak ditemukan dengan id: " + kebunId);
        requireUserWithRole(mandorId, Role.MANDOR, "Mandor");

        if (assignmentRepository.kebunHasMandor(kebunId)) {
            throw new KebunConflictException(
                    "Kebun sudah memiliki Mandor yang ditugaskan");
        }

        if (assignmentRepository.mandorIsAssigned(mandorId)) {
            throw new KebunConflictException(
                    "Mandor sudah ditugaskan ke kebun lain");
        }

        assignmentRepository.assignMandor(kebunId, mandorId);
    }

    @Override
    @Transactional
    public void reassignMandor(Long mandorId, String fromKebunId, String toKebunId) {
        requireKebunExists(fromKebunId, "Kebun asal tidak ditemukan dengan id: " + fromKebunId);
        requireKebunExists(toKebunId, "Kebun tujuan tidak ditemukan dengan id: " + toKebunId);
        requireUserWithRole(mandorId, Role.MANDOR, "Mandor");

        String currentKebunId = assignmentRepository.findKebunIdByMandorId(mandorId)
                .orElseThrow(() -> new KebunConflictException(
                        "Mandor belum ditugaskan ke kebun manapun"));

        if (!currentKebunId.equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Mandor tidak ditugaskan di kebun asal yang disebutkan");
        }

        if (assignmentRepository.kebunHasMandor(toKebunId)) {
            throw new KebunConflictException(
                    "Kebun tujuan sudah memiliki Mandor yang ditugaskan");
        }

        assignmentRepository.moveMandor(mandorId, fromKebunId, toKebunId);
    }

    @Override
    @Transactional
    public void assignSupir(String kebunId, Long supirId) {
        requireKebunExists(kebunId, "Kebun tidak ditemukan dengan id: " + kebunId);
        requireUserWithRole(supirId, Role.SUPIR, "Supir Truk");

        if (assignmentRepository.supirIsAssigned(supirId)) {
            throw new KebunConflictException(
                    "Supir Truk sudah ditugaskan ke kebun lain");
        }

        assignmentRepository.assignSupir(kebunId, supirId);
    }

    @Override
    @Transactional
    public void reassignSupir(Long supirId, String fromKebunId, String toKebunId) {
        requireKebunExists(fromKebunId, "Kebun asal tidak ditemukan dengan id: " + fromKebunId);
        requireKebunExists(toKebunId, "Kebun tujuan tidak ditemukan dengan id: " + toKebunId);
        requireUserWithRole(supirId, Role.SUPIR, "Supir Truk");

        String currentKebunId = assignmentRepository.findKebunIdBySupirId(supirId)
                .orElseThrow(() -> new KebunConflictException(
                        "Supir Truk belum ditugaskan ke kebun manapun"));

        if (!currentKebunId.equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Supir Truk tidak ditugaskan di kebun asal yang disebutkan");
        }

        assignmentRepository.moveSupir(supirId, fromKebunId, toKebunId);
    }

    private void requireKebunExists(String kebunId, String message) {
        kebunRepository.findById(kebunId)
                .orElseThrow(() -> new KebunNotFoundException(message));
    }

    private void requireUserWithRole(Long userId, Role expectedRole, String roleLabel) {
        UserSnapshot user = userReader.findUserById(userId)
                .orElseThrow(() -> new KebunNotFoundException(
                        "User tidak ditemukan dengan id: " + userId));

        if (user.getRole() != expectedRole) {
            throw new KebunConflictException(
                    "User dengan id " + userId
                            + " bukan " + roleLabel
                            + " (role: " + user.getRole() + ")");
        }
    }
}
