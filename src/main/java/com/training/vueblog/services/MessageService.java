package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.MessageRepository;
import com.training.vueblog.repositories.TagRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MessageService {

  private final MessageRepository messageRepository;

  private final Storage storage;

  private final TagRepository tagRepository;

  public MessageService(MessageRepository messageRepository, Storage storage, TagRepository tagRepository) {
    this.messageRepository = messageRepository;
    this.storage = storage;
    this.tagRepository = tagRepository;
  }

  public List<Message> getAllMessages(String filter, Boolean findByTag, Pageable pageable) {
    List<Message> messages;

    if (filter != null && !filter.isEmpty()) {
      if (findByTag) {
        messages = messageRepository.findAll(pageable)
                  .stream().filter(p -> p.getTags()
            .stream().anyMatch(t -> t.getContent().contains(filter))).collect(
            Collectors.toList());
      } else {
        messages = messageRepository.findAllByBodyContains(filter, pageable).getContent();
      }
    } else {
      messages = messageRepository.findAll(pageable).getContent();
    }
    System.out.println(messages.size() + " " + pageable.getPageNumber());
    return messages;
  }

  public Message getMessage(String id) {
    return messageRepository.findById(id).orElse(null);
  }

  public Message addMessage(User user, Message message, MultipartFile file) throws IOException {

    message.setId(UUID.randomUUID().toString());
    message.setCreationDate(LocalDateTime.now());
    message.setUser(user);

    for (Tag tag : message.getTags()) {
      if (tagRepository.getByContent(tag.getContent()) == null) {
        tag.setNumberOfMessages(1);
      } else {
        tag = tagRepository.getByContent(tag.getContent());
        tag.setNumberOfMessages(tag.getNumberOfMessages() + 1);
      }
      tagRepository.save(tag);
    }
    List<Tag> tags = message.getTags();
    message.setTags(new ArrayList<>());
    for (Tag tag : tags) {
      message.getTags().add(tagRepository.getByContent(tag.getContent()));
    }

    if (!file.isEmpty()) {
      String link = message.getPhotoLink();
      String photoId = UUID.randomUUID() + link.substring(link.indexOf("."));
      BlobId blobId = BlobId.of("vueblog-files-bucket", photoId);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      storage.create(blobInfo, file.getBytes());
      message.setPhotoLink("https://storage.googleapis.com/vueblog-files-bucket/" + photoId);
    }

    messageRepository.save(message);

    return message;
  }

  public void deleteMessage(User user, String id) {
    Message message = messageRepository.findById(id).orElse(null);

    if (message != null && user.getId().equals(message.getUser().getId())) {
      String photoLink = message.getPhotoLink();
      List<Tag> tags = message.getTags();

      if (photoLink != null) {
        System.out.println(photoLink.substring(photoLink.lastIndexOf("/") + 1));
        BlobId blobId = BlobId.of("vueblog-files-bucket", photoLink.substring(photoLink.lastIndexOf("/") + 1));
        storage.delete(blobId);
      }
      messageRepository.delete(message);

      if (tags.size() > 0) {
        for (Tag value : tags) {
          Tag tag = tagRepository.getByContent(value.getContent());
          if (tag == null)
            continue;
          if (tag.getNumberOfMessages() == 1) {
            tagRepository.delete(tag);
          } else {
            tag.setNumberOfMessages(tag.getNumberOfMessages() - 1);
            tagRepository.save(tag);
          }
        }
      }
    }
  }

  public ResponseEntity<Message> like(User user, String id) {
    messageRepository.findById(id).ifPresent(message -> {
      message.getUserLikes().add(user.getId());
      messageRepository.save(message);
    });

    return new ResponseEntity<>(HttpStatus.OK);
  }

  public ResponseEntity<Message> unlike(User user, String id) {
    messageRepository.findById(id).ifPresent(message -> {
      message.getUserLikes().remove(user.getId());
      messageRepository.save(message);
    });

    return new ResponseEntity<>(HttpStatus.OK);
  }

  public List<Message> getUserMessages(String username, Pageable pageable) {
    return messageRepository.findAllByUserUsername(username, pageable).getContent();
  }
}
