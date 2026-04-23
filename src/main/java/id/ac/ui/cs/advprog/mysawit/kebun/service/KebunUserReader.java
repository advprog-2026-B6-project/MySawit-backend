package id.ac.ui.cs.advprog.mysawit.kebun.service;

import java.util.List;
import java.util.Optional;

public interface KebunUserReader {
    Optional<UserSnapshot> findUserById(Long userId);
    List<UserSnapshot> findUsersByRole(String role);
    List<UserSnapshot> findUsersByIds(List<Long> userIds);
}
