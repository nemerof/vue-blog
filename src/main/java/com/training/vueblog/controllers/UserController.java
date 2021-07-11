package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/api/login")
public class UserController {

    private final AuthenticationManager authManager;

    private final UserRepository userRepository;

    public UserController(AuthenticationManager authManager, UserRepository userRepository) {
        this.authManager = authManager;
        this.userRepository = userRepository;
    }

    //todo Complete rework of methods (used for user authentication => creating principal and http session)
    @PostMapping
    public User authorize(@RequestBody User user, HttpServletRequest req) {
        User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);

        if (dbUser == null)
            return new User();

        login(req, user.getUsername(), user.getPassword());

        return dbUser;
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
