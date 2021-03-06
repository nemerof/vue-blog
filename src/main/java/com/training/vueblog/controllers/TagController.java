package com.training.vueblog.controllers;

import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.objects.dto.TagDTO;
import com.training.vueblog.services.TagService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/popular")
  public List<TagDTO> getPopularTags() {
    return tagService.getPopularTags();
  }

  @GetMapping("/popular/{inputPattern}")
  public List<TagDTO> getPopularTagsByPattern(@PathVariable String inputPattern) {
    return tagService.getPopularTagsByPattern(inputPattern);
  }

  @GetMapping("/{tag}")
  public TagDTO getTag(@PathVariable String tag) {
    return tagService.getTag(tag);
  }

  @GetMapping("/messages/{tag}")
  public List<MessageDTO> getTagMessages(@PathVariable String tag,
    @PageableDefault(sort = {"creationDate"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
    return tagService.getTagMessages(tag, pageable);
  }

}
