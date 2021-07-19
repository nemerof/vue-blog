package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.MessageRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.training.vueblog.repositories.TagRepository;
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

  public List<Message> getAllMessages(String filter, Boolean findByTag) {
    List<Message> messages;
    if (filter != null && !filter.isEmpty()) {
      if (findByTag) {
        messages = messageRepository.findAll();
        ListIterator<Message> iterator = messages.listIterator();
        while (iterator.hasNext()) {
          List<Tag> tags = iterator.next().getTags();
          boolean isAlright = false;
          for (Tag tag : tags) {
            if (tag.getContent().contains(filter)) {
              isAlright = true;
              break;
            }
          }
          if (!isAlright)
            iterator.remove();
        }
      } else {
        messages = messageRepository.findAllByBodyContains(filter);
      }
    } else {
      messages = messageRepository.findAll();
    }
    Collections.reverse(messages);
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
      if (tagRepository.getByContent(tag.getContent()) == null)
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
      if (photoLink != null) {
        System.out.println(photoLink.substring(photoLink.lastIndexOf("/") + 1));
        BlobId blobId = BlobId.of("vueblog-files-bucket", photoLink.substring(photoLink.lastIndexOf("/") + 1));
        storage.delete(blobId);
      }
      messageRepository.delete(message);
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

}
