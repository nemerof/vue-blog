package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.services.TagService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping
    public User subToTag(@AuthenticationPrincipal User user,
                         @RequestParam String tag) {
        return tagService.subToTag(user, tag);
    }
}
