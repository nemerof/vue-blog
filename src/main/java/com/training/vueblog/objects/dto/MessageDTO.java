package com.training.vueblog.objects.dto;

import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class MessageDTO {

  private final String id;
  private final String body;
  private final LocalDateTime creationDate;
  private final String photoLink;
  private final String username;
  private final List<String> tagsContent;
  private final Set<String> userLikes;

  public MessageDTO(String id, String body, LocalDateTime creationDate, String photoLink,
    String username, List<String> tagsContent, Set<String> userLikes) {
    this.id = id;
    this.body = body;
    this.creationDate = creationDate;
    this.photoLink = photoLink;
    this.username = username;
    this.tagsContent = tagsContent;
    this.userLikes = userLikes;
  }

  public MessageDTO(Message message) {
    id = message.getId();
    body = message.getBody();
    creationDate = message.getCreationDate();
    photoLink = message.getPhotoLink();
    username = message.getUser().getUsername();

    List<String> tc = new ArrayList<>();
    for(Tag t : message.getTags()) tc.add(t.getContent());

    tagsContent = tc;
    userLikes = message.getUserLikes();
  }
}
