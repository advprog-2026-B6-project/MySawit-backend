package id.ac.ui.cs.advprog.mysawit.service;

import id.ac.ui.cs.advprog.mysawit.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long id);
}
