package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import com.training.vueblog.services.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

//todo class for user login processing
@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
      this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<User> authorize(@RequestBody User user, HttpServletRequest req) {
      return loginService.authorize(user, req);
    }
}
