package com.training.vueblog.controllers;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.services.MessageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


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

    //todo change test
    @GetMapping
    public List<MessageDTO> getAllMessages(@AuthenticationPrincipal User user,
      @RequestParam(required = false) String filter,
      @RequestParam(name = "bytag", required = false) Boolean findByTag,
      @PageableDefault(sort = {"creationDate"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
      if(user != null && !findByTag && filter.isEmpty()) {
        return messageService.getAllMessagesForAuthUser(user, pageable);
      }
      return messageService.getAllMessages(filter, findByTag, pageable);
    }

    @GetMapping("/user/{username}")
    public List<MessageDTO> getUserMessages(@PathVariable String username,
      @PageableDefault(sort = {"creationDate"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) {

      return messageService.getUserMessages(username, pageable);
    }

    @GetMapping("/{id}")
    public MessageDTO getMessage(@PathVariable String id) {
        return messageService.getMessage(id);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*") // ???? ???????????? ?????? ?????? ??????????)
    @PostMapping("/add")
    public Message addMessage(@AuthenticationPrincipal User user,
                              @RequestPart("text") Message message,
                              @RequestPart("tags") List<Tag> messageTags,
                              @RequestParam("file") MultipartFile file) throws IOException {

      return messageService.addMessage(user, message, messageTags, file);
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
