package com.training.vueblog.controllers;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.MessageRepository;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageRepository messageRepository;

    private final Storage storage;

    @Autowired
    public MessageController(MessageRepository messageRepository, Storage storage) {
        this.messageRepository = messageRepository;
        this.storage = storage;
    }

    //example of adding file to google storage
    @GetMapping("/add")
    public void method() throws IOException {
        BlobId blobId = BlobId.of("vueblog-files-bucket", "result.txt");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        File file = new File("D:\\copy\\result.txt");
        byte[] data = Files.readAllBytes(Paths.get(file.toURI()));
        storage.create(blobInfo, data);
    }

    @GetMapping
    public List<Message> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        Collections.reverse(messages);
        return messages;
    }

    @GetMapping("/{id}")
    public Message getOne(@PathVariable String id) {

        return messageRepository.findById(id).orElse(null);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*") // не уверен что это нужно)
    @PostMapping("/add")
    public Message addMessage(@AuthenticationPrincipal User user,
                              @RequestPart("text") Message message,
                              @RequestParam("file") MultipartFile file) throws IOException {
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

    @DeleteMapping("/{id}")
    public void deleteMessage(@AuthenticationPrincipal User user,
                              @PathVariable String id) {
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
    
    @GetMapping("/like/{id}")
    public ResponseEntity<Message> like(@AuthenticationPrincipal User user,
                     @PathVariable String id) {
        messageRepository.findById(id).ifPresent(message -> {
            message.getUserLikes().add(user.getId());
            messageRepository.save(message);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/unlike/{id}")
    public ResponseEntity<Message> unlike(@AuthenticationPrincipal User user,
                                 @PathVariable String id) {
        messageRepository.findById(id).ifPresent(message -> {
            message.getUserLikes().remove(user.getId());
            messageRepository.save(message);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
