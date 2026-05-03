package id.ac.ui.cs.advprog.mysawit.auth.controller;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;

import id.ac.ui.cs.advprog.mysawit.auth.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> dto = userService.getUserById(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/example")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> example(@AuthenticationPrincipal UserDetails auth) {

        UserDto user = userService.getUserByUsername(auth.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String username = user.getUsername(); 
        Role role = user.getRole(); 

        // pake getter setter untuk ngambil/ngubah data
        // getUsername(), getApalah()
        
        return ResponseEntity.ok(username + " adalah " + role);

    }
}
