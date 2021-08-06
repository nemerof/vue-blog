package com.training.vueblog.controllers;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.MessageRepository;
import com.training.vueblog.services.MessageService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

    private final Storage storage;

    private final MessageService messageService;

    @Autowired
    public MessageController(Storage storage,
      MessageService messageService) {
        this.storage = storage;
        this.messageService = messageService;
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
    public List<Message> getAllMessages(@RequestParam(required = false) String filter,
                                        @RequestParam(name = "bytag", required = false) Boolean findByTag,
                                        @PageableDefault(sort = {"creationDate"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
        return messageService.getAllMessages(filter, findByTag, pageable);
    }

    @GetMapping("/{id}")
    public Message getMessage(@PathVariable String id) {
        return messageService.getMessage(id);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*") // не уверен что это нужно)
    @PostMapping("/add")
    public Message addMessage(@AuthenticationPrincipal User user,
                              @RequestPart("text") Message message,
                              @RequestParam("file") MultipartFile file) throws IOException {

      return messageService.addMessage(user, message, file);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@AuthenticationPrincipal User user,
                              @PathVariable String id) {
        messageService.deleteMessage(user, id);
    }

    @GetMapping("/like/{id}")
    public ResponseEntity<Message> like(@AuthenticationPrincipal User user,
                     @PathVariable String id) {
        return messageService.like(user, id);
    }

    @GetMapping("/unlike/{id}")
    public ResponseEntity<Message> unlike(@AuthenticationPrincipal User user,
                                 @PathVariable String id) {
        return messageService.unlike(user, id);
    }
}

class TestClass {

}
