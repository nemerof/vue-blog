package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
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

    private final AuthenticationManager authManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public LoginController(AuthenticationManager authManager, UserRepository userRepository, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping
    public ResponseEntity<User> authorize(@RequestBody User user, HttpServletRequest req) {
        User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);

        if (dbUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY); // nonexistent user
        }

        if (!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // wrong password
        }

        dbUser.setLastVisit(LocalDateTime.now());
        userRepository.save(dbUser);

        login(req, user.getUsername(), user.getPassword());
        return new ResponseEntity<>(dbUser, HttpStatus.OK);
    }

    // actual creation of Authentication Principal
    public void login(HttpServletRequest req, String user, String pass) {
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(user, pass);
        Authentication auth = authManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    }
}
