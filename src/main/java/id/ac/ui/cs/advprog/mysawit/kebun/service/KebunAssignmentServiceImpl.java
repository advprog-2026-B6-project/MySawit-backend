package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KebunAssignmentServiceImpl implements KebunAssignmentService {

    private final KebunSawitRepository kebunRepository;
    private final KebunMandorJpaRepository kebunMandorRepository;
    private final KebunSupirJpaRepository kebunSupirRepository;
    private final KebunUserReader userReader;

    public KebunAssignmentServiceImpl(KebunSawitRepository kebunRepository,
                                      KebunMandorJpaRepository kebunMandorRepository,
                                      KebunSupirJpaRepository kebunSupirRepository,
                                      KebunUserReader userReader) {
        this.kebunRepository = kebunRepository;
        this.kebunMandorRepository = kebunMandorRepository;
        this.kebunSupirRepository = kebunSupirRepository;
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
        if (kebunMandorRepository.existsByKebunId(kebunId)) {
            throw new KebunConflictException(
                    "Kebun sudah memiliki Mandor yang ditugaskan");
        }

        // Check mandor isn't already assigned to another kebun
        if (kebunMandorRepository.existsByMandorId(mandorId)) {
            throw new KebunConflictException(
                    "Mandor sudah ditugaskan ke kebun lain");
        }

        KebunMandorEntity assignment = new KebunMandorEntity();
        assignment.setKebunId(kebunId);
        assignment.setMandorId(mandorId);
        kebunMandorRepository.save(assignment);
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
        KebunMandorEntity currentAssignment = kebunMandorRepository.findByMandorId(mandorId)
                .orElseThrow(() -> new KebunConflictException(
                        "Mandor belum ditugaskan ke kebun manapun"));

        if (!currentAssignment.getKebunId().equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Mandor tidak ditugaskan di kebun asal yang disebutkan");
        }

        // Validate toKebun doesn't already have a mandor
        if (kebunMandorRepository.existsByKebunId(toKebunId)) {
            throw new KebunConflictException(
                    "Kebun tujuan sudah memiliki Mandor yang ditugaskan");
        }

        // Atomic swap: delete old + create new
        kebunMandorRepository.delete(currentAssignment);

        KebunMandorEntity newAssignment = new KebunMandorEntity();
        newAssignment.setKebunId(toKebunId);
        newAssignment.setMandorId(mandorId);
        kebunMandorRepository.save(newAssignment);
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
        if (kebunSupirRepository.existsBySupirId(supirId)) {
            throw new KebunConflictException(
                    "Supir Truk sudah ditugaskan ke kebun lain");
        }

        KebunSupirEntity assignment = new KebunSupirEntity();
        assignment.setKebunId(kebunId);
        assignment.setSupirId(supirId);
        kebunSupirRepository.save(assignment);
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
        KebunSupirEntity currentAssignment = kebunSupirRepository.findBySupirId(supirId)
                .orElseThrow(() -> new KebunConflictException(
                        "Supir Truk belum ditugaskan ke kebun manapun"));

        if (!currentAssignment.getKebunId().equals(fromKebunId)) {
            throw new KebunConflictException(
                    "Supir Truk tidak ditugaskan di kebun asal yang disebutkan");
        }

        // Atomic swap: delete old + create new
        kebunSupirRepository.delete(currentAssignment);

        KebunSupirEntity newAssignment = new KebunSupirEntity();
        newAssignment.setKebunId(toKebunId);
        newAssignment.setSupirId(supirId);
        kebunSupirRepository.save(newAssignment);
    }
}
