package id.ac.ui.cs.advprog.mysawit.kebun.service;

import id.ac.ui.cs.advprog.mysawit.model.Role;
import id.ac.ui.cs.advprog.mysawit.model.User;
import id.ac.ui.cs.advprog.mysawit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KebunUserReaderAdapter implements KebunUserReader {

    private final UserRepository userRepository;

    public KebunUserReaderAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserSnapshot> findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toSnapshot);
    }

    @Override
    public List<UserSnapshot> findUsersByRole(String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == roleEnum)
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSnapshot> findUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private UserSnapshot toSnapshot(User user) {
        return new UserSnapshot(
                user.getId(),
                user.getFullname(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getCertificationNumber()
        );
    }
}
