package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;

import java.util.List;
import java.util.UUID;

public interface SupirTrukService {

    List<SupirTruk> getDaftarSupirBertugas();
    List<SupirTruk> getAllSupirTruk();
    SupirTruk getSupirTrukById(UUID id);
    SupirTruk tambahSupirTruk(SupirTruk supirTruk);
    SupirTruk updateStatusBertugas(UUID id, boolean sedangBertugas);
}
