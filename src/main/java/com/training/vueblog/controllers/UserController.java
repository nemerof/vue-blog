package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.UserDTO;
import com.training.vueblog.services.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/api/users")
  public List<UserDTO> getUsers() {
    return userService.getUsers();
  }

  @DeleteMapping("/api/users/{id}")
  public void deleteUser(@AuthenticationPrincipal User user,
    @PathVariable String id) {
    userService.deleteUser(id, user);
  }

  @GetMapping("/api/users/{inputPattern}")
  public List<UserDTO> getUsersByPattern(@PathVariable String inputPattern) {
    return userService.getUsersByPattern(inputPattern);
  }

  @GetMapping("/api/subscribe")
  public User subscribe(@AuthenticationPrincipal User user, @RequestParam String username) {
    return userService.subscribe(user, username);
  }

  @GetMapping("/api/subscribers/{user}")
  public Set<UserDTO> getSubscribersByPattern(
    @PathVariable String user, @RequestParam String inputPattern) {
    return userService.getSubscribersByPattern(user, inputPattern);
  }

  @GetMapping("/api/subscriptions/{user}")
  public Set<UserDTO> getSubscriptionsByPattern(
    @PathVariable String user, @RequestParam String inputPattern) {
    return userService.getSubscriptionsByPattern(user, inputPattern);
  }

  @GetMapping("/api/subscriptions-count/{user}")
  public Integer getSubscriptionsCount(@PathVariable String user) {
    return userService.getSubscriptionsCount(user);
  }

  @GetMapping("/api/subscribers-count/{user}")
  public Integer getSubscribersCount(@PathVariable String user) {
    return userService.getSubscribersCount(user);
  }

  //todo add test
  @GetMapping("/api/user-photo-link/{user}")
  public UserDTO getUserPhotoLink(@PathVariable String user) {
    return userService.getUserPhotoLink(user);
  }

}
