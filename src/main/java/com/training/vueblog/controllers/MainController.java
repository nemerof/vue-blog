package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class MainController {
    @GetMapping
    public Map<Object, Object> main(@AuthenticationPrincipal User user) {
        Map<Object, Object> data = new HashMap<>();

        data.put("profile", user);
        data.put("messages", null);

        return data;
    }
}
