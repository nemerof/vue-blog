package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.MessageRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MessageService {

  private final MessageRepository messageRepository;

  private final Storage storage;

  public MessageService(MessageRepository messageRepository, Storage storage) {
    this.messageRepository = messageRepository;
    this.storage = storage;
  }

  public List<Message> getAllMessages() {
    List<Message> messages = messageRepository.findAll();
    Collections.reverse(messages);
    return messages;
  }

  public Message getOne(String id) {
    return messageRepository.findById(id).orElse(null);
  }

  public Message addMessage(User user, Message message, MultipartFile file) throws IOException {

    message.setId(UUID.randomUUID().toString());
    message.setCreationDate(LocalDateTime.now());
    message.setUser(user);

    if (!file.isEmpty()) {
      String link = message.getPhotoLink();
      String photoId = UUID.randomUUID().toString() + link.substring(link.indexOf("."));
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
