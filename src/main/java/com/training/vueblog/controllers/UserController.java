package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.services.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping("/api/users-except-current")
  public List<User> getUsersExceptCurrent(@AuthenticationPrincipal User user) {
    return userService.getUsersExceptCurrent(user);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping("/api/users-except-current/{inputPattern}")
  public List<User> getFilteredUsersExceptCurrent(
    @AuthenticationPrincipal User user, @PathVariable String inputPattern) {
    return userService.getFilteredUsersExceptCurrent(user, inputPattern);
  }

  @DeleteMapping("/api/users/{id}")
  public void deleteUser(
//    @AuthenticationPrincipal User user,
    @PathVariable String id) {
    userService.deleteUser(id);
  }

  @GetMapping
  @RequestMapping("/api/subscribers")
  public Set<User> getSubscribers(@AuthenticationPrincipal User user) {
    return userService.getSubscribers(user);
  }

  @GetMapping
  @RequestMapping("/api/subscriptions")
  public Set<User> getSubscriptions(@AuthenticationPrincipal User user) {
    return userService.getSubscriptions(user);
  }

  @GetMapping
  @RequestMapping("/api/users/{inputPattern}")
  public List<User> getUsersByPattern(@PathVariable String inputPattern) {
    return userService.getUsersByPattern(inputPattern);
  }

  @GetMapping
  @RequestMapping("/api/subscriptions/{inputPattern}")
  public Set<User> getSubscriptionsByPattern(@AuthenticationPrincipal User user, @PathVariable String inputPattern) {
    return userService.getSubscriptionsByPattern(user, inputPattern);
  }
}
