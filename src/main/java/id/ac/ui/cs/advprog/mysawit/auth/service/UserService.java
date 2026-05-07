package id.ac.ui.cs.advprog.mysawit.auth.service;

import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;

public interface UserService {
    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long id);

    Optional<UserDto> getUserByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserDto> deleteUserById(Long id);

    Optional<UserDto> assignBuruhToMandor(String buruhUsername, String mandorUsername);

    Optional<UserDto> reassignBuruhToMandor(String buruhUsername, String newMandorUsername);
}
