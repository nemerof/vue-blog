package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.services.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @RequestMapping("/api/users")
  public List<User> getUsers() {
    return userService.getUsers();
  }

  @GetMapping
  @RequestMapping("/api/subscriptions")
  public Set<User> getSubscriptions(@AuthenticationPrincipal User user) {
    return userService.getSubscriptions(user);
  }
}