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

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestPart("user") User user,
                                             @RequestParam("file") MultipartFile file)
      throws IOException {

        return userService.registerUser(user, file);
    }

    @GetMapping
    public User getUser(@AuthenticationPrincipal User user) {
        return userService.getUser(user);
    }
}
