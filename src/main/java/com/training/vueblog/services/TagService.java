package com.training.vueblog.services;


import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;

  private final UserRepository userRepository;

  private final MessageService messageService;
  private final UserService userService;

  public TagService(TagRepository tagRepository,
                    UserRepository userRepository, MessageService messageService, UserService userService) {
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.userService = userService;
  }

  public User subToTag(User user, String tagContent) {
    User dbUser = userService.getUser(user);
    Tag tag = tagRepository.getByContent(tagContent);
    if (dbUser.getSubTags().contains(tag)) {
      dbUser.getSubTags().remove(tag);
    } else {
      dbUser.getSubTags().add(tag);
    }
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
