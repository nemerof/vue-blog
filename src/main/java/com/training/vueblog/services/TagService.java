package com.training.vueblog.services;


import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.objects.dto.TagDTO;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
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

  public List<TagDTO> getPopularTags() {
    List<Tag> popularTags = tagRepository.findAll();
    popularTags.sort(Tag::compareTo);

    return getTagDTOList(popularTags);
  }

  public List<TagDTO> getTagDTOList(List<Tag> tags) {
    List<TagDTO> tagDTOList = new ArrayList<>();

    for (Tag t : tags) {
      tagDTOList.add(new TagDTO(t));
    }
    return tagDTOList;
  }

  public List<TagDTO> getPopularTagsByPattern(String inputPattern) {
    List<Tag> popularTags = tagRepository.findAllByContentContains(inputPattern);
    if(popularTags.size() > 1) popularTags.sort(Tag::compareTo);
    return getTagDTOList(popularTags);
  }

  public List<MessageDTO> getTagMessages(String tag, Pageable pageable) {
    return messageService.getAllMessages(tag, true, pageable);
  }

  public TagDTO getTag(String tag) {
    return new TagDTO(tagRepository.getByContent(tag));
  }
}
