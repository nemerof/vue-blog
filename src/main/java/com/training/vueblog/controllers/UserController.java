package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.services.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
//  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping("/api/users-other-subscriptions")
  public List<User> getUsersExceptCurrentSubscriptions(
    @AuthenticationPrincipal User user, @RequestParam String inputPattern) {
    return userService.getUsersExceptCurrentSubscriptions(user, inputPattern);
  }

  @RequestMapping("/api/users-other-subscribers")
  public List<User> getUsersExceptCurrentSubscribers(
    @AuthenticationPrincipal User user, @RequestParam String inputPattern) {
    return userService.getUsersExceptCurrentSubscribers(user, inputPattern);
  }


  @DeleteMapping("/api/users/{id}")
  public void deleteUser(
//    @AuthenticationPrincipal User user,
    @PathVariable String id) {
    userService.deleteUser(id);
  }

  @GetMapping
  @RequestMapping("/api/users/{inputPattern}")
  public List<User> getUsersByPattern(@PathVariable String inputPattern) {
    return userService.getUsersByPattern(inputPattern);
  }

  @GetMapping
  @RequestMapping("/api/subscribe")
  public User subscribe(@AuthenticationPrincipal User user, @RequestParam String username) {
    return userService.subscribe(user, username);
  }

  @GetMapping
  @RequestMapping("/api/subscribers/{user}")
  public Set<User> getSubscribersByPattern(
    @PathVariable String user, @RequestParam String inputPattern) {
    return userService.getSubscribersByPattern(user, inputPattern);
  }

  @GetMapping
  @RequestMapping("/api/subscriptions/{user}")
  public Set<User> getSubscriptionsByPattern(
    @PathVariable String user, @RequestParam String inputPattern) {
    return userService.getSubscriptionsByPattern(user, inputPattern);
  }

}
