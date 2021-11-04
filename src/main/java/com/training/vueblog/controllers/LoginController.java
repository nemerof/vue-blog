package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.services.LoginService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
