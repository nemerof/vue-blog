package com.training.vueblog.services;

import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;

  private final UserRepository userRepository;

  public TagService(TagRepository tagRepository,
    UserRepository userRepository) {
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
  }

  public User subToTag(User user, String tag) {
    User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);
    dbUser.getSubTags().add(tagRepository.getByContent(tag));

    userRepository.save(dbUser);

    return dbUser;
  }
}
