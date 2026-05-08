package id.ac.ui.cs.advprog.mysawit.pembayaran.repository;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    @Query("SELECT p FROM Payroll p WHERE p.username = :username " +
           "AND (:startDate IS NULL OR p.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.endDate <= :endDate)")
    List<Payroll> findByUsernameAndDateFilter(
            @Param("username") String username,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Payroll p WHERE p.username = :username " +
           "AND (:startDate IS NULL OR p.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.endDate <= :endDate) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Payroll> findByUsernameAndFilter(
            @Param("username") String username,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);
}
