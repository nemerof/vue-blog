package com.training.vueblog.controllers;

import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import com.training.vueblog.services.UserService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<User> registerUser(@RequestPart("user") User user,
                                             @RequestParam("file") MultipartFile file)
      throws IOException {
        User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);
        if (dbUser != null) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED); // user with such username already exists
        }

        userService.createUser(user, file);

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
