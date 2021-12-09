package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.repositories.MessageRepository;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MessageService {

  private final UserRepository userRepository;

  private final MessageRepository messageRepository;

  private final Storage storage;

  private final TagRepository tagRepository;

  public MessageService(UserRepository userRepository,
    MessageRepository messageRepository, Storage storage, TagRepository tagRepository) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.storage = storage;
    this.tagRepository = tagRepository;
  }

  //todo: change test
  public List<MessageDTO> getAllMessages(String filter, Boolean findByTag, Pageable pageable) {
    List<Message> messages = new ArrayList<>();

    if (filter != null && !filter.isEmpty()) {
      int pageElement = pageable.getPageNumber() * 5;
      int numberOfNewPosts = pageElement + 5;
      if (findByTag) {
        Tag tag = tagRepository.getByContent(filter);
        if(tag != null)
        messages = messageRepository.findAllByTagsContains(tag);

      }
      else {
        messages = messageRepository.findAllByBodyContains(filter);
      }
      if(pageElement > messages.size()) return new ArrayList<>();
      if(messages.subList(pageElement, messages.size()).size() < 5) {
        numberOfNewPosts = pageElement + messages.size() - pageElement;
      }
      messages.sort(Comparator.comparing(Message::getCreationDate).reversed());
      messages = messages.subList(pageElement, numberOfNewPosts);
    } else {
      messages = messageRepository.findAll(pageable).getContent();
    }

    return getMessageDTOList(messages);
  }

  public List<MessageDTO> getMessageDTOList(List<Message> messages) {
    List<MessageDTO> messageDTOList = new ArrayList<>();

    for (Message m : messages) {
      messageDTOList.add(new MessageDTO(m));
    }
    return messageDTOList;
  }

  public MessageDTO getMessage(String id) {
    Message message = messageRepository.findById(id).orElse(null);
    if(message != null) {
      return new MessageDTO(message);
    } return null;
  }

  //todo: add test
  public List<MessageDTO> getAllMessagesForAuthUser(User user, Pageable pageable) {
    user = userRepository.findByUsername(user.getUsername());

    Set<MessageDTO> messages = new HashSet<>(getUserMessages(user.getUsername()));

    for (User u : user.getSubscriptions()) {
      messages.addAll(getMessageDTOList(messageRepository.findAllByUserUsername(u.getUsername())));
    }
    for (Tag t : user.getSubTags()) {
      messages.addAll(getAllMessages(t.getContent(), true, PageRequest.of(0, 5)));
    }

    List<MessageDTO> messagesList = new ArrayList<>(messages);
    messagesList.sort(Comparator.comparing(MessageDTO::getCreationDate).reversed());


    int pageElement = pageable.getPageNumber() * 5;
    if(pageElement > messagesList.size()) return new ArrayList<>();
    int numberOfNewPosts = pageElement + 5;
    if(messagesList.subList(pageElement, messages.size()).size() < 5) {
      numberOfNewPosts = pageElement + messages.size() - pageElement;
    }
    messagesList = messagesList.subList(pageElement, numberOfNewPosts);

    return messagesList;
  }

  public Message addMessage(User user, Message message, List<Tag> messageTags, MultipartFile file) throws IOException {

    message.setId(UUID.randomUUID().toString());
    message.setCreationDate(LocalDateTime.now());
    message.setUser(user);

    for (int i = 0; i < messageTags.size(); i++) {
      Tag tag = messageTags.get(i);
      if (tagRepository.getByContent(tag.getContent()) == null) {
        tag.setId(UUID.randomUUID().toString());
        tag.setNumberOfMessages(1);
      } else {
        tag = tagRepository.getByContent(tag.getContent());
        tag.setNumberOfMessages(tag.getNumberOfMessages() + 1);
      }
      messageTags.set(i, tag);
      tagRepository.save(tag);
    }
    message.setTags(new ArrayList<>());
    message.setTags(messageTags);

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

    if (message != null && (user.getId().equals(message.getUser().getId()) || user.getRoles().contains(
      Role.ADMIN))) {
      String photoLink = message.getPhotoLink();
//      List<String> tags = message.getTags();

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

  public List<MessageDTO> getUserMessages(String username, Pageable pageable) {
    List<Message> messages = messageRepository.findAllByUserUsername(username, pageable).getContent();
    return getMessageDTOList(messages);
  }

  public List<MessageDTO> getUserMessages(String username) {
    List<Message> messages = messageRepository.findAllByUserUsername(username);
    return getMessageDTOList(messages);
  }
}
