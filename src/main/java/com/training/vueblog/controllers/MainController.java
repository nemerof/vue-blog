package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

// todo class for work with user data on frontend
@RestController
@RequestMapping("/api/user")
public class MainController {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public MainController(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping
    // todo Rework registering of the user
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        user.setCreationDate(LocalDateTime.now());
        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    // used for getting user data for frontend
    public User getUser(@AuthenticationPrincipal User user) {
        if (user != null)
            System.out.println(user.getPassword());
        else
            System.out.println("No principal");
        return user;
    }
}
