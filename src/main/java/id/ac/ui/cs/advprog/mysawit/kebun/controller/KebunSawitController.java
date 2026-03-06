package id.ac.ui.cs.advprog.mysawit.kebun.controller;

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
}
