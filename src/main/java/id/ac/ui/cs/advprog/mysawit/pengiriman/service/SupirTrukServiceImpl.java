package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.SupirTruk;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.SupirTrukRepository;

@Service
public class SupirTrukServiceImpl implements SupirTrukService {

    private final SupirTrukRepository supirTrukRepository;
    private final UserRepository userRepository;

    public SupirTrukServiceImpl(SupirTrukRepository supirTrukRepository,
                                UserRepository userRepository) {
        this.supirTrukRepository = supirTrukRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<SupirTruk> getDaftarSupirBertugas() {
        return supirTrukRepository.findAllBertugas();
    }

    @Override
    public List<SupirTruk> getAllSupirTruk() {
        // Fallback: if in-memory repo is empty, query registered SUPIR users
        List<SupirTruk> fromRepo = supirTrukRepository.findAll();
        if (!fromRepo.isEmpty()) {
            return fromRepo;
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SUPIR)
                .map(this::toSupirTruk)
                .toList();
    }

    private SupirTruk toSupirTruk(User user) {
        return SupirTruk.builder()
                .id(UUID.nameUUIDFromBytes(user.getUsername().getBytes()))
                .nama(user.getFullname())
                .platNomorTruk(user.getCertificationNumber() != null ? user.getCertificationNumber() : "-")
                .sedangBertugas(false)
                .build();
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
