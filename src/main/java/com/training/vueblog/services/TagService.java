package com.training.vueblog.services;


import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;

  private final UserRepository userRepository;

  private final MessageService messageService;

  public TagService(TagRepository tagRepository,
    UserRepository userRepository, MessageService messageService) {
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  public User subToTag(User user, String tag) {
    User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);
    dbUser.getSubTags().add(tagRepository.getByContent(tag));
    userRepository.save(dbUser);
    return dbUser;
  }

  public List<Tag> getPopularTags() {
    return tagRepository.findAll().stream().sorted(Tag::compareTo)
      .collect(Collectors.toList());
  }

  public List<Message> getTagMessages(String tag, Pageable pageable) {
    return messageService.getAllMessages(tag, true, pageable);
  }

  public Tag getTag(String tag) {
    return tagRepository.getByContent(tag);
  }
}
