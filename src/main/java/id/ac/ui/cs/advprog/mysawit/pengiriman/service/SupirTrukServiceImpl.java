package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupirTrukServiceImpl implements SupirTrukService {

    private final SupirTrukRepository supirTrukRepository;

    public SupirTrukServiceImpl(SupirTrukRepository supirTrukRepository) {
        this.supirTrukRepository = supirTrukRepository;
    }

    @Override
    public List<SupirTruk> getDaftarSupirBertugas() {
        return supirTrukRepository.findAllBertugas();
    }

    @Override
    public List<SupirTruk> getAllSupirTruk() {
        return supirTrukRepository.findAll();
    }

    @Override
    public SupirTruk getSupirTrukById(UUID id) {
        return supirTrukRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supir truk tidak ditemukan"));
    }

    @Override
    public SupirTruk tambahSupirTruk(SupirTruk supirTruk) {
        return supirTrukRepository.save(supirTruk);
    }

    @Override
    public SupirTruk updateStatusBertugas(UUID id, boolean sedangBertugas) {
        SupirTruk supirTruk = supirTrukRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supir truk tidak ditemukan"));
        supirTruk.setSedangBertugas(sedangBertugas);
        return supirTrukRepository.save(supirTruk);
    }
}
