package com.training.vueblog.objects.dto;

import com.training.vueblog.objects.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TagDTO {

  private final String id;
  private final String content;
  private final int numberOfMessages;
  private final int numberOfSubscribers;

  public TagDTO(String id, String content, int numberOfMessages, int numberOfSubscribers) {
    this.id = id;
    this.content = content;
    this.numberOfMessages = numberOfMessages;
    this.numberOfSubscribers = numberOfSubscribers;
  }

  public TagDTO(Tag tag) {
    id = tag.getId();
    content = tag.getContent();
    numberOfMessages = tag.getNumberOfMessages();
    numberOfSubscribers = tag.getSubscribers().size();
  }
}
