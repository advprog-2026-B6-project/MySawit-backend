package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentRequest;
import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.PengirimanAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.mapper.PengirimanAssignmentMapper;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;

@Service
public class PengirimanAssignmentServiceImpl implements PengirimanAssignmentService {

    private final PengirimanAssignmentRepository repository;

    public PengirimanAssignmentServiceImpl(PengirimanAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PengirimanAssignmentResponse createAssignment(PengirimanAssignmentRequest request) {
        validateRequest(request);
        PengirimanAssignment assignment = PengirimanAssignmentMapper.toEntity(request);
        PengirimanAssignment saved = repository.save(assignment);
        return PengirimanAssignmentMapper.toResponse(saved);
    }

    @Override
    public List<PengirimanAssignmentResponse> getAllAssignments() {
        return repository.findAll().stream()
                .map(PengirimanAssignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateRequest(PengirimanAssignmentRequest request) {
        if (request.getMandorEmail() == null || request.getMandorEmail().isBlank()) {
            throw new IllegalArgumentException("Mandor email wajib diisi");
        }
        if (request.getSupirEmail() == null || request.getSupirEmail().isBlank()) {
            throw new IllegalArgumentException("Supir email wajib diisi");
        }
        if (request.getMuatanKg() <= 0 || request.getMuatanKg() > 400) {
            throw new IllegalArgumentException("Muatan harus antara 0 - 400 kg");
        }
        if (request.getTujuan() == null || request.getTujuan().isBlank()) {
            throw new IllegalArgumentException("Tujuan wajib diisi");
        }
    }
}
