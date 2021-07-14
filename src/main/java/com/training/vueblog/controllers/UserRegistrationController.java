package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import com.training.vueblog.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// todo class for work with user registration
@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    private final UserService userService;

    private final UserRepository userRepository;

    public UserRegistrationController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);
        if (dbUser != null) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED); // user with such username already exists
        }

        userService.createUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public User getUser(@AuthenticationPrincipal User user) {
        if (user != null)
            System.out.println(user.getPassword());
        else
            System.out.println("No principal");
        return user;
    }
}
