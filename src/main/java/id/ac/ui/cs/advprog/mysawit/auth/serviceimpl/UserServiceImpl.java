package id.ac.ui.cs.advprog.mysawit.auth.serviceimpl;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final String MANDOR_ASSIGNED_TO_BURUH_MESSAGE =
            "We cant delete that users as its been assigned to a Buruh";

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDto::new)
                .toList();
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::new);
    }

    @Override
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDto::new);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserDto> deleteUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        Optional<UserDto> dtoOpt = userOpt.map(UserDto::new);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == Role.MANDOR
                    && userRepository.existsByRoleAndMandorUsername(Role.BURUH, user.getUsername())) {
                throw new IllegalStateException(MANDOR_ASSIGNED_TO_BURUH_MESSAGE);
            }
            userRepository.deleteById(id);
        }
        return dtoOpt;
    }

    @Override
    public Optional<UserDto> assignBuruhToMandor(String buruhUsername, String mandorUsername) {
        Optional<User> buruhOpt = userRepository.findByUsername(buruhUsername);
        Optional<User> mandorOpt = userRepository.findByUsername(mandorUsername);
        if (buruhOpt.isEmpty() || mandorOpt.isEmpty()) {
            return Optional.empty();
        }
        User buruh = buruhOpt.get();
        User mandor = mandorOpt.get();
        if (buruh.getRole() != Role.BURUH) {
            throw new IllegalArgumentException("Target user is not a BURUH");
        }
        if (mandor.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException("Assigned supervisor is not a MANDOR");
        }
        if (buruh.getMandorUsername() != null && !buruh.getMandorUsername().isBlank()) {
            throw new IllegalStateException("Buruh is already assigned to a Mandor");
        }
        buruh.setMandorUsername(mandor.getUsername());
        userRepository.save(buruh);
        return Optional.of(new UserDto(buruh));
    }

    @Override
    public Optional<UserDto> reassignBuruhToMandor(String buruhUsername, String newMandorUsername) {
        Optional<User> buruhOpt = userRepository.findByUsername(buruhUsername);
        Optional<User> mandorOpt = userRepository.findByUsername(newMandorUsername);
        if (buruhOpt.isEmpty() || mandorOpt.isEmpty()) {
            return Optional.empty();
        }
        User buruh = buruhOpt.get();
        User newMandor = mandorOpt.get();
        if (buruh.getRole() != Role.BURUH) {
            throw new IllegalArgumentException("Target user is not a BURUH");
        }
        if (newMandor.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException("Assigned supervisor is not a MANDOR");
        }
        buruh.setMandorUsername(newMandor.getUsername());
        userRepository.save(buruh);
        return Optional.of(new UserDto(buruh));
    }

}
