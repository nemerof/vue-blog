package com.training.vueblog.controllers;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.repositories.MessageRepository;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

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
        return messageRepository.findAll();
    }

    @GetMapping("/{id}")
    public Message getOne(@PathVariable String id) {

        return messageRepository.findById(id).orElse(null);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*") // не уверен что это нужно)
    @PostMapping("/add")
    public Message addMessage(@RequestPart("text") Message message, @RequestParam("file") MultipartFile file2)
      throws IOException {
      message.setId(UUID.randomUUID().toString());
      message.setCreationDate(LocalDateTime.now());

      messageRepository.save(message);

      return message;
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable String id) {
        messageRepository.deleteById(id);
    }
}
