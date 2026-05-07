package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong admin!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        Optional<UserDto> deleted = userService.deleteUserById(id);
        return deleted.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
