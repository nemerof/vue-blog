package com.training.vueblog.controllers;

import com.training.vueblog.objects.Message;
import com.training.vueblog.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @GetMapping("/{id}")
    public Message getOne(@PathVariable String id) {

        return messageRepository.findById(id).orElse(null);
    }

    @PostMapping("/add")
    public Message addMessage(@RequestBody Message message) {
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
