package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.dto.CreateKebunRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunDetailResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.KebunResponseMapper;
import id.ac.ui.cs.advprog.mysawit.kebun.dto.UpdateKebunRequest;
import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.service.KebunSawitService;
import jakarta.validation.Valid;
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
    private final KebunResponseMapper mapper;

    public KebunSawitController(KebunSawitService service, KebunResponseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<KebunResponse> create(@Valid @RequestBody CreateKebunRequest request) {
        KebunSawit kebun = mapper.toDomain(request);
        KebunSawit created = service.create(kebun);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<KebunResponse>> getAll(
            @RequestParam(required = false, defaultValue = "") String nama,
            @RequestParam(required = false, defaultValue = "") String kode) {
        return ResponseEntity.ok(mapper.toResponses(service.findAll(nama, kode)));
    }

    @GetMapping("/{kodeUnik}")
    public ResponseEntity<Object> getByKodeUnik(@PathVariable String kodeUnik) {
        return service.findByKodeUnik(kodeUnik)
                .map(mapper::toResponse)
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
    public ResponseEntity<KebunResponse> update(@PathVariable String id, @Valid @RequestBody UpdateKebunRequest request) {
        KebunSawit kebun = mapper.toDomain(request);
        KebunSawit updated = service.update(id, kebun);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
