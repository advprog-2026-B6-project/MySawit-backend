package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

public interface PengirimanAssignmentRepository extends JpaRepository<PengirimanAssignment, Long> {
    List<PengirimanAssignment> findByMandorEmail(String mandorEmail);
    List<PengirimanAssignment> findBySupirEmail(String supirEmail);
    List<PengirimanAssignment> findByMandorEmailAndSupirEmail(String mandorEmail, String supirEmail);
}
