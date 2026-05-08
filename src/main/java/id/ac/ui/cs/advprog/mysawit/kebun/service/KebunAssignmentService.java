package id.ac.ui.cs.advprog.mysawit.kebun.service;

public interface KebunAssignmentService {

    // Mandor operations
    void assignMandor(String kebunId, Long mandorId);
    void reassignMandor(Long mandorId, String fromKebunId, String toKebunId);

    // Supir operations
    void assignSupir(String kebunId, Long supirId);
    void reassignSupir(Long supirId, String fromKebunId, String toKebunId);
}
