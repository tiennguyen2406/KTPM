package com.movie.ticket.user.controller;

import com.movie.ticket.user.entity.User;
import com.movie.ticket.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        log.info("REST request to register user: {}", user.getUsername());
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        log.info("REST request to login user: {}", username);
        try {
            User user = userService.login(username, password);
            log.info("Login successful for user: {}", username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.warn("Login failed for user: {} - {}", username, e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
