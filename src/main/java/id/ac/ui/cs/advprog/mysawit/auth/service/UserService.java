package id.ac.ui.cs.advprog.mysawit.auth.service;

import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;

public interface UserService {
    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long id);
}
