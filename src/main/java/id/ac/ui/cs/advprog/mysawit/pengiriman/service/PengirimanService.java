package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.Pengiriman;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

import java.util.List;
import java.util.UUID;

public interface PengirimanService {

    Pengiriman buatPengiriman(UUID mandorId, UUID supirTrukId, double muatanKg, String tujuan);
    Pengiriman ubahStatusPengiriman(UUID pengirimanId, UUID supirTrukId, StatusPengiriman statusBaru);
    List<Pengiriman> getDaftarPengirimanSupir(UUID supirTrukId);
    List<Pengiriman> getDaftarPengirimanBerlangsung();
    Pengiriman getPengirimanById(UUID id);
    List<Pengiriman> getAllPengiriman();
}
