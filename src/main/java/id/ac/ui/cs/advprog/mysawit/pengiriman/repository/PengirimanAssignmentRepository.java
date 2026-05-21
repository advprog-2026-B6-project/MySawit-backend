package id.ac.ui.cs.advprog.mysawit.pengiriman.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;

public interface PengirimanAssignmentRepository extends JpaRepository<PengirimanAssignment, Long> {
    List<PengirimanAssignment> findByMandorEmail(String mandorEmail);
    List<PengirimanAssignment> findBySupirEmail(String supirEmail);
    List<PengirimanAssignment> findByMandorEmailAndSupirEmail(String mandorEmail, String supirEmail);

    @Query("""
            SELECT a FROM PengirimanAssignment a
            WHERE a.approval = :approval
            AND (:mandorQuery = '' OR LOWER(COALESCE(a.mandorEmail, '')) LIKE LOWER(CONCAT('%', :mandorQuery, '%')))
            AND (:startAt IS NULL OR a.createdAt >= :startAt)
            AND (:endAt IS NULL OR a.createdAt <= :endAt)
            """)
    List<PengirimanAssignment> findApprovedAssignmentsForAdmin(
            @Param("approval") ApprovalAssignment approval,
            @Param("mandorQuery") String mandorQuery,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);
}
