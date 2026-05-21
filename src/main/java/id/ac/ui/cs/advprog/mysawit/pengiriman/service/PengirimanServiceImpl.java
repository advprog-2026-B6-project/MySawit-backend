package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.assignment.PengirimanAssignmentAdminService;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.shared.SupirIdentityMapper;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.workflow.PengirimanWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PengirimanServiceImpl implements PengirimanService {

    private final PengirimanWorkflowService workflowService;
    private final PengirimanAssignmentAdminService assignmentAdminService;

    @Autowired
    public PengirimanServiceImpl(PengirimanWorkflowService workflowService,
                                 PengirimanAssignmentAdminService assignmentAdminService) {
        this.workflowService = workflowService;
        this.assignmentAdminService = assignmentAdminService;
    }

    public PengirimanServiceImpl(PengirimanRepository pengirimanRepository,
                                 PengirimanAssignmentRepository pengirimanAssignmentRepository,
                                 SupirTrukRepository supirTrukRepository,
                                 UserRepository userRepository,
                                 PayrollRequestSender payrollRequestSender) {
        this.workflowService = new PengirimanWorkflowService(
                pengirimanRepository, supirTrukRepository, userRepository, payrollRequestSender);
        this.assignmentAdminService = new PengirimanAssignmentAdminService(
                pengirimanAssignmentRepository, userRepository, payrollRequestSender, new SupirIdentityMapper());
    }

    @Override
    public Pengiriman buatPengiriman(Long mandorId, UUID supirTrukId, double muatanKg, String tujuan) {
        return workflowService.buatPengiriman(mandorId, supirTrukId, muatanKg, tujuan);
    }

    @Override
    public Pengiriman ubahStatusPengiriman(UUID pengirimanId, UUID supirTrukId, StatusPengiriman statusBaru) {
        return workflowService.ubahStatusPengiriman(pengirimanId, supirTrukId, statusBaru);
    }

    @Override
    public Pengiriman setujuiPengiriman(UUID pengirimanId, Long mandorId) {
        return workflowService.setujuiPengiriman(pengirimanId, mandorId);
    }

    @Override
    public Pengiriman tolakPengiriman(UUID pengirimanId, Long mandorId, String alasanPenolakan) {
        return workflowService.tolakPengiriman(pengirimanId, mandorId, alasanPenolakan);
    }

    @Override
    public Pengiriman setujuiPengirimanAdmin(UUID pengirimanId, Long adminId) {
        return workflowService.setujuiPengirimanAdmin(pengirimanId, adminId);
    }

    @Override
    public Pengiriman tolakPengirimanAdmin(UUID pengirimanId, Long adminId, String alasanPenolakan) {
        return workflowService.tolakPengirimanAdmin(pengirimanId, adminId, alasanPenolakan);
    }

    @Override
    public Pengiriman tolakPengirimanParsialAdmin(UUID pengirimanId,
                                                   Long adminId,
                                                   double muatanKgDiakui,
                                                   String alasanPenolakan) {
        return workflowService.tolakPengirimanParsialAdmin(pengirimanId, adminId, muatanKgDiakui, alasanPenolakan);
    }

    @Override
    public PengirimanAssignment setujuiAssignmentFinalAdmin(Long assignmentId, Long adminId) {
        return assignmentAdminService.setujuiAssignmentFinalAdmin(assignmentId, adminId);
    }

    @Override
    public PengirimanAssignment tolakAssignmentFinalAdmin(Long assignmentId, Long adminId, String alasanPenolakan) {
        return assignmentAdminService.tolakAssignmentFinalAdmin(assignmentId, adminId, alasanPenolakan);
    }

    @Override
    public PengirimanAssignment tolakAssignmentFinalParsialAdmin(Long assignmentId,
                                                                  Long adminId,
                                                                  double muatanKgDiakui,
                                                                  String alasanPenolakan) {
        return assignmentAdminService.tolakAssignmentFinalParsialAdmin(
                assignmentId, adminId, muatanKgDiakui, alasanPenolakan);
    }

    @Override
    public List<Pengiriman> getDaftarPengirimanSupir(UUID supirTrukId) {
        return workflowService.getDaftarPengirimanSupir(supirTrukId);
    }

    @Override
    public List<Pengiriman> getRiwayatPengirimanSupir(UUID supirTrukId, LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        return workflowService.getRiwayatPengirimanSupir(supirTrukId, tanggalMulai, tanggalSelesai);
    }

    @Override
    public String getAlasanPenolakan(UUID pengirimanId, UUID supirTrukId) {
        return workflowService.getAlasanPenolakan(pengirimanId, supirTrukId);
    }

    @Override
    public List<ApprovedPengirimanResponse> getPengirimanDisetujui(String mandorName,
                                                                    LocalDate tanggalMulai,
                                                                    LocalDate tanggalSelesai) {
        return assignmentAdminService.getPengirimanDisetujui(mandorName, tanggalMulai, tanggalSelesai);
    }

    @Override
    public List<Pengiriman> getDaftarPengirimanBerlangsung() {
        return workflowService.getDaftarPengirimanBerlangsung();
    }

    @Override
    public Pengiriman getPengirimanById(UUID id) {
        return workflowService.getPengirimanById(id);
    }

    @Override
    public List<Pengiriman> getAllPengiriman() {
        return workflowService.getAllPengiriman();
    }
}
