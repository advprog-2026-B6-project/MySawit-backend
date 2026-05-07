package id.ac.ui.cs.advprog.mysawit.auth.serviceimpl;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
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
        if (dtoOpt.isPresent()) {
            userRepository.deleteById(id);
        }
        return dtoOpt;
    }

}
