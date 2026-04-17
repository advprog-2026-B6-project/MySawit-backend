package id.ac.ui.cs.advprog.mysawit.pembayaran.repository;

import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WageSettingRepository extends JpaRepository<WageSetting, String> {
}