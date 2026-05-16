package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

public interface PengirimanService {

    Pengiriman buatPengiriman(Long mandorId, UUID supirTrukId, double muatanKg, String tujuan);
    Pengiriman ubahStatusPengiriman(UUID pengirimanId, UUID supirTrukId, StatusPengiriman statusBaru);
    Pengiriman setujuiPengiriman(UUID pengirimanId, Long mandorId);
    Pengiriman tolakPengiriman(UUID pengirimanId, Long mandorId, String alasanPenolakan);
    Pengiriman setujuiPengirimanAdmin(UUID pengirimanId, Long adminId);
    Pengiriman tolakPengirimanAdmin(UUID pengirimanId, Long adminId, String alasanPenolakan);
    Pengiriman tolakPengirimanParsialAdmin(UUID pengirimanId,
                                           Long adminId,
                                           double muatanKgDiakui,
                                           String alasanPenolakan);
    List<Pengiriman> getDaftarPengirimanSupir(UUID supirTrukId);
    List<Pengiriman> getRiwayatPengirimanSupir(UUID supirTrukId,
                                               java.time.LocalDate tanggalMulai,
                                               java.time.LocalDate tanggalSelesai);
    String getAlasanPenolakan(UUID pengirimanId, UUID supirTrukId);
    List<id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApprovedPengirimanResponse> getPengirimanDisetujui(
        String mandorName,
        java.time.LocalDate tanggalMulai,
        java.time.LocalDate tanggalSelesai);
    List<Pengiriman> getDaftarPengirimanBerlangsung();
    Pengiriman getPengirimanById(UUID id);
    List<Pengiriman> getAllPengiriman();
}

