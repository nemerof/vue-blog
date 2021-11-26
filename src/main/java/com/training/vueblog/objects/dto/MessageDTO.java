package com.training.vueblog.objects.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MessageDTO {

  private final String id;
  private final String body;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private final LocalDateTime creationDate;
  private final String photoLink;
  private final String username;
  private final String userPhotoLink;
  private final List<String> tags;
  private final Set<String> userLikes;

  public MessageDTO(Message message) {
    id = message.getId();
    body = message.getBody();
    creationDate = message.getCreationDate();
    photoLink = message.getPhotoLink();
    username = message.getUser().getUsername();
    userPhotoLink = message.getUser().getPhotoLink();

    if(message.getTags() != null) {
      List<String> tc = new ArrayList<>();
      for (Tag t : message.getTags())
        tc.add(t.getContent());
      tags = tc;
    } else tags = null;
    userLikes = message.getUserLikes();
  }
}
