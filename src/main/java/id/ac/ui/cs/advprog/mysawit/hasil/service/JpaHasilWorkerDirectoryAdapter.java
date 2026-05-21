package id.ac.ui.cs.advprog.mysawit.hasil.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;

@Component
public class JpaHasilWorkerDirectoryAdapter implements HasilWorkerDirectory {
    private final UserRepository userRepository;

    public JpaHasilWorkerDirectoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Set<String> findSupervisedWorkerIds(String mandorUsername) {
        return userRepository.findAll().stream()
                .filter(user -> mandorUsername.equals(user.getMandorUsername()))
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isWorkerSupervisedBy(String workerId, String mandorUsername) {
        return userRepository.findByUsername(workerId)
                .map(user -> mandorUsername.equals(user.getMandorUsername()))
                .orElse(false);
    }

    @Override
    public String resolveWorkerName(String workerId) {
        return userRepository.findByUsername(workerId)
                .map(User::getFullname)
                .filter(fullname -> fullname != null && !fullname.isBlank())
                .orElse(workerId);
    }
}
