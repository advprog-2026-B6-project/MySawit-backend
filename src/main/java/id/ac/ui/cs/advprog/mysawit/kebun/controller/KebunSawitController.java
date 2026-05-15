package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.CreateKebunRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.UpdateKebunRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunSawitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kebun")
@CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
public class KebunSawitController {

    private final KebunSawitService service;

    public KebunSawitController(KebunSawitService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateKebunRequest request) {
        KebunSawit kebun = toKebunSawit(request);
        KebunSawit created = service.create(kebun);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<KebunSawit>> getAll(
            @RequestParam(required = false, defaultValue = "") String nama,
            @RequestParam(required = false, defaultValue = "") String kode) {
        return ResponseEntity.ok(service.findAll(nama, kode));
    }

    @GetMapping("/{kodeUnik}")
    public ResponseEntity<Object> getByKodeUnik(@PathVariable String kodeUnik) {
        return service.findByKodeUnik(kodeUnik)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Kebun tidak ditemukan: " + kodeUnik)));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> getDetail(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "") String searchSupir) {
        KebunDetailResponse detail = service.getDetail(id, searchSupir);
        return ResponseEntity.ok(detail);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody UpdateKebunRequest request) {
        KebunSawit kebun = toKebunSawit(request);
        KebunSawit updated = service.update(id, kebun);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private KebunSawit toKebunSawit(CreateKebunRequest request) {
        KebunSawit kebun = new KebunSawit();
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setKodeUnik(request.getKodeUnik());
        kebun.setKiriAtas(request.getKiriAtas());
        kebun.setKiriBawah(request.getKiriBawah());
        kebun.setKananAtas(request.getKananAtas());
        kebun.setKananBawah(request.getKananBawah());
        return kebun;
    }

    private KebunSawit toKebunSawit(UpdateKebunRequest request) {
        KebunSawit kebun = new KebunSawit();
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setKiriAtas(request.getKiriAtas());
        kebun.setKiriBawah(request.getKiriBawah());
        kebun.setKananAtas(request.getKananAtas());
        kebun.setKananBawah(request.getKananBawah());
        return kebun;
    }
}
