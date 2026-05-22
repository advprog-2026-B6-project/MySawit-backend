package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.DeleteUserResponse;
import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable Long id) {
        try {
            Optional<UserDto> deleted = userService.deleteUserById(id);
            return deleted.map((user) -> ResponseEntity.ok(new DeleteUserResponse(user)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new DeleteUserResponse(e.getMessage()));
        }
    }

    @PostMapping("/assign/{buruhUsername}/{mandorUsername}")
    public ResponseEntity<UserDto> assignBuruhToMandor(@PathVariable String buruhUsername,
            @PathVariable String mandorUsername) {
        try {
            Optional<UserDto> result = userService.assignBuruhToMandor(buruhUsername, mandorUsername);
            if (result.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(result.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/reassign/{buruhUsername}/{newMandorUsername}")
    public ResponseEntity<UserDto> reassignBuruh(@PathVariable String buruhUsername,
            @PathVariable String newMandorUsername) {
        try {
            Optional<UserDto> result = userService.reassignBuruhToMandor(buruhUsername, newMandorUsername);
            if (result.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(result.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
