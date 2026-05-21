package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.MandorInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.SupirInfo;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.model.Coordinate;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunMandorJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirEntity;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSupirJpaRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KebunSawitServiceImpl implements KebunSawitService {

    private static final String kodeUnixRegex = "^[A-Z]{2}-\\d{4}$";

    private final KebunSawitRepository repository;
    private final KebunMandorJpaRepository kebunMandorRepository;
    private final KebunSupirJpaRepository kebunSupirRepository;
    private final KebunUserReader userReader;

    public KebunSawitServiceImpl(KebunSawitRepository repository,
                                 KebunMandorJpaRepository kebunMandorRepository,
                                 KebunSupirJpaRepository kebunSupirRepository,
                                 KebunUserReader userReader) {
        this.repository = repository;
        this.kebunMandorRepository = kebunMandorRepository;
        this.kebunSupirRepository = kebunSupirRepository;
        this.userReader = userReader;
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

        // Validasi 4 koordinat membentuk persegi
        if (!isValidSquare(kebun.getKiriAtas(), kebun.getKiriBawah(), 
                           kebun.getKananAtas(), kebun.getKananBawah())) {
            throw new IllegalArgumentException("Keempat koordinat yang dimasukkan tidak membentuk persegi sempurna");
        }

        // Hitung luas berdasarkan koordinat (distSq mengembalikan luas dalam meter persegi)
        double luasMeterPersegi = distSq(kebun.getKiriAtas(), kebun.getKiriBawah());
        double luasHektare = luasMeterPersegi / 10000.0;
        kebun.setLuasHektare(luasHektare);

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

    @Override
    public KebunSawit update(String id, KebunSawit updatedKebun) {
        KebunSawit existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kebun tidak ditemukan dengan id: " + id));

        // Validasi nama kebun tidak null
        if (updatedKebun.getNamaKebun() == null) {
            throw new IllegalArgumentException("Nama kebun tidak boleh null");
        }

        // Validasi 4 koordinat tidak null
        if (updatedKebun.getKiriAtas() == null || updatedKebun.getKiriBawah() == null
                || updatedKebun.getKananAtas() == null || updatedKebun.getKananBawah() == null) {
            throw new IllegalArgumentException("Semua 4 koordinat harus diisi");
        }

        // Validasi koordinat membentuk persegi
        if (!isValidSquare(updatedKebun.getKiriAtas(), updatedKebun.getKiriBawah(),
                           updatedKebun.getKananAtas(), updatedKebun.getKananBawah())) {
            throw new IllegalArgumentException("Keempat koordinat yang dimasukkan tidak membentuk persegi sempurna");
        }

        // Hitung luas berdasarkan koordinat baru
        double luasMeterPersegi = distSq(updatedKebun.getKiriAtas(), updatedKebun.getKiriBawah());
        double luasHektare = luasMeterPersegi / 10000.0;

        // Cek overlap dengan semua kebun lain (kecuali diri sendiri)
        for (KebunSawit other : repository.findAll()) {
            if (other.getId().equals(id)) continue;
            if (OverlapValidator.isOverlapping(
                    updatedKebun.getKoordinatAsList(),
                    other.getKoordinatAsList())) {
                throw new IllegalArgumentException(
                        "Kebun overlap dengan kebun: " + other.getNamaKebun()
                                + " (" + other.getKodeUnik() + ")");
            }
        }

        // Lock kodeUnik: always keep original
        existing.setNamaKebun(updatedKebun.getNamaKebun());
        existing.setLuasHektare(luasHektare);
        existing.setKiriAtas(updatedKebun.getKiriAtas());
        existing.setKiriBawah(updatedKebun.getKiriBawah());
        existing.setKananAtas(updatedKebun.getKananAtas());
        existing.setKananBawah(updatedKebun.getKananBawah());

        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kebun tidak ditemukan dengan id: " + id));

        // Cek apakah kebun masih memiliki Mandor yang ditugaskan
        if (kebunMandorRepository.existsByKebunId(id)) {
            throw new IllegalArgumentException(
                    "Tidak dapat menghapus kebun yang masih memiliki Mandor yang ditugaskan");
        }

        repository.deleteById(id);
    }

    @Override
    public KebunDetailResponse getDetail(String kebunId, String searchSupirNama) {
        KebunSawit kebun = repository.findById(kebunId)
                .orElseThrow(() -> new IllegalArgumentException("Kebun tidak ditemukan dengan id: " + kebunId));

        // Resolve Mandor info
        MandorInfo mandorInfo = kebunMandorRepository.findByKebunId(kebunId)
                .flatMap(assignment -> userReader.findUserById(assignment.getMandorId()))
                .map(snapshot -> new MandorInfo(
                        snapshot.getId(),
                        snapshot.getFullname(),
                        snapshot.getCertificationNumber()))
                .orElse(null);

        // Resolve Supir list
        List<KebunSupirEntity> supirAssignments = kebunSupirRepository.findAllByKebunId(kebunId);
        List<Long> supirIds = supirAssignments.stream()
                .map(KebunSupirEntity::getSupirId)
                .collect(Collectors.toList());

        List<SupirInfo> supirList;
        if (supirIds.isEmpty()) {
            supirList = new ArrayList<>();
        } else {
            supirList = userReader.findUsersByIds(supirIds).stream()
                    .map(snapshot -> new SupirInfo(snapshot.getId(), snapshot.getFullname()))
                    .collect(Collectors.toList());
        }

        // Apply search filter on supir names
        if (searchSupirNama != null && !searchSupirNama.isEmpty()) {
            String lowerSearch = searchSupirNama.toLowerCase();
            supirList = supirList.stream()
                    .filter(s -> s.getFullname() != null
                            && s.getFullname().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        return new KebunDetailResponse(
                kebun.getId(),
                kebun.getNamaKebun(),
                kebun.getKodeUnik(),
                kebun.getLuasHektare(),
                kebun.getKiriAtas(),
                kebun.getKiriBawah(),
                kebun.getKananAtas(),
                kebun.getKananBawah(),
                mandorInfo,
                supirList
        );
    }

    private boolean isValidSquare(Coordinate kiriAtas, Coordinate kiriBawah, 
                                  Coordinate kananAtas, Coordinate kananBawah) {
        // Hitung kuadrat jarak dari 4 sisi yang bersebelahan
        double sisiKiri = distSq(kiriAtas, kiriBawah);
        double sisiKanan = distSq(kananAtas, kananBawah);
        double sisiAtas = distSq(kiriAtas, kananAtas);
        double sisiBawah = distSq(kiriBawah, kananBawah);

        // Persegi valid jika: 
        // 1. Jaraknya lebih dari 0 (bukan titik yang menumpuk di koordinat yang sama)
        // 2. Keempat sisinya sama panjang
        return sisiKiri > 0 && 
               sisiKiri == sisiKanan && 
               sisiKanan == sisiAtas && 
               sisiAtas == sisiBawah;
    }

    // Menghitung kuadrat jarak
    private double distSq(Coordinate p1, Coordinate p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return (dx * dx) + (dy * dy);
    }
}
