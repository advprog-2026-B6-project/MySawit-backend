package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
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
    public ResponseEntity<Object> create(@RequestBody KebunSawit kebun) {
        try {
            KebunSawit created = service.create(kebun);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            e.printStackTrace(); // DEBUG: Print exception stack trace to console
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
        try {
            KebunDetailResponse detail = service.getDetail(id, searchSupir);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody KebunSawit kebun) {
        try {
            KebunSawit updated = service.update(id, kebun);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("masih memiliki Mandor")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
