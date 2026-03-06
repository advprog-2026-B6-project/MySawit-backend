package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KebunSawitServiceImpl implements KebunSawitService {

    private static final String kodeUnixRegex = "^[A-Z]{2}-\\d{4}$";

    private final KebunSawitRepository repository;

    public KebunSawitServiceImpl(KebunSawitRepository repository) {
        this.repository = repository;
    }

    @Override
    public KebunSawit create(KebunSawit kebun) {
        // Validasi format kodeUnik (XX-0000)
        if (kebun.getKodeUnik() == null || !kebun.getKodeUnik().matches(kodeUnixRegex)) {
            throw new IllegalArgumentException(
                    "Format kode unik tidak valid. Gunakan format: XX-0000 (contoh: KB-0001)");
        }

        // Cek kodeUnik sudah ada atau belum
        if (repository.findByKodeUnik(kebun.getKodeUnik()).isPresent()) {
            throw new IllegalArgumentException(
                    "Kode unik kebun sudah digunakan: " + kebun.getKodeUnik());
        }

        // Validasi nama kebun tidak null
        if (kebun.getNamaKebun() == null) {
            throw new IllegalArgumentException("Nama kebun tidak boleh null");
        }

        // Validasi 4 koordinat tidak null
        if (kebun.getKiriAtas() == null || kebun.getKiriBawah() == null
                || kebun.getKananAtas() == null || kebun.getKananBawah() == null) {
            throw new IllegalArgumentException("Semua 4 koordinat harus diisi");
        }

        // Validasi luas kebun tidak null
        if (kebun.getLuasHektare() == null) {
            throw new IllegalArgumentException("Luas kebun tidak boleh null");
        }

        // Validasi luas kebun tidak negatif
        if (kebun.getLuasHektare() < 0) {
            throw new IllegalArgumentException("Luas kebun tidak boleh negatif");
        }

        // Validasi luas kebun tidak 0
        if (kebun.getLuasHektare() == 0) {
            throw new IllegalArgumentException("Luas kebun tidak boleh 0");
        }

        // Cek overlap dengan semua kebun yang sudah ada
        for (KebunSawit existing : repository.findAll()) {
            if (OverlapValidator.isOverlapping(
                    kebun.getKoordinatAsList(),
                    existing.getKoordinatAsList())) {
                throw new IllegalArgumentException(
                        "Kebun overlap dengan kebun: " + existing.getNamaKebun()
                                + " (" + existing.getKodeUnik() + ")");
            }
        }

        // Generate UUID
        kebun.setId(UUID.randomUUID().toString());

        // Simpan
        return repository.save(kebun);
    }

    @Override
    public List<KebunSawit> findAll(String searchNama, String searchKode) {
        return repository.findAll().stream()
                .filter(k -> searchNama == null || searchNama.isEmpty()
                        || k.getNamaKebun().toLowerCase().contains(searchNama.toLowerCase()))
                .filter(k -> searchKode == null || searchKode.isEmpty()
                        || k.getKodeUnik().toLowerCase().contains(searchKode.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<KebunSawit> findByKodeUnik(String kodeUnik) {
        return repository.findByKodeUnik(kodeUnik);
    }
}
