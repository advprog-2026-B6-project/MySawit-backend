package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.kebun.model.KebunSawit;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KebunSearchService {

    private final KebunSawitRepository repository;

    public KebunSearchService(KebunSawitRepository repository) {
        this.repository = repository;
    }

    public List<KebunSawit> findAll(String searchNama, String searchKode) {
        return repository.search(searchNama, searchKode);
    }
}
