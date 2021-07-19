package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    private final UserRepository userRepository;

    public TagController(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public User subToTag(@AuthenticationPrincipal User user,
                         @RequestParam String tag) {
        user.getSubTags().add(tagRepository.getByContent(tag));

        userRepository.save(user);

        return user;
    }
}
