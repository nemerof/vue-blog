package com.training.vueblog.services;

import static com.training.vueblog.data.MessageTestData.MESSAGE1;
import static com.training.vueblog.data.MessageTestData.MESSAGE2;
import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.repositories.MessageRepository;
import com.training.vueblog.repositories.TagRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private Storage storage;
  @Mock
  private TagRepository tagRepository;
  @InjectMocks
  private MessageService messageService;

  @Test
  void getMessageDTOList() {
    List<MessageDTO> messageDTOListFromService =
      messageService.getMessageDTOList(Arrays.asList(MESSAGE1, MESSAGE2));
    List<MessageDTO> messageDTOList = Arrays.asList(new MessageDTO(MESSAGE1), new MessageDTO(MESSAGE2));
    assertEquals(messageDTOList, messageDTOListFromService);
  }

  @Test
  void getAllMessagesNoFilterNotByTagTest() {
    Page<Message> page = new PageImpl<>(Arrays.asList(MESSAGE1, MESSAGE2));
    when(messageRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);
    List<MessageDTO> messagesFromService =
      messageService.getAllMessages(null, false, PageRequest.of(0, 5));
    List<MessageDTO> messages = Arrays.asList(new MessageDTO(MESSAGE1), new MessageDTO(MESSAGE2));
    assertEquals(messages, messagesFromService);
    verify(messageRepository, times(1)).findAll(PageRequest.of(0, 5));
  }

  @Test
  void getAllMessagesFilterNotByTagTest() {
    List<Message> messages = Arrays.asList(MESSAGE1);
    when(messageRepository.findAllByBodyContains("Body 1")).thenReturn(messages);
    List<MessageDTO> messagesFromServiceWithFilter =
      messageService.getAllMessages("Body 1", false, PageRequest.of(0, 5));
    List<MessageDTO> messagesWithFilter = List.of(new MessageDTO(MESSAGE1));
    assertEquals(messagesWithFilter, messagesFromServiceWithFilter);

    verify(messageRepository, times(1)).findAllByBodyContains("Body 1");
  }

  @Test
  void getAllMessagesFilterByTagTest() {
    List<Message> messages = Arrays.asList(MESSAGE1);

    when(tagRepository.getByContent("tag1")).thenReturn(TAG1);
    when(messageRepository.findAllByTagsContains(TAG1)).thenReturn(messages);

    List<MessageDTO> messagesFromServiceWithFilterByTag =
      messageService.getAllMessages("tag1", true, PageRequest.of(0, 5));
    List<MessageDTO> messagesWithFilterByTag = List.of(new MessageDTO(MESSAGE1));
    assertEquals(messagesWithFilterByTag, messagesFromServiceWithFilterByTag);

    verify(tagRepository, times(1)).getByContent("tag1");
    verify(messageRepository, times(1)).findAllByTagsContains(TAG1);
  }

  @Test
  void getMessageTest() {
    when(messageRepository.findById(MESSAGE1.getId())).thenReturn(Optional.of(MESSAGE1));
    MessageDTO messageFromService = messageService.getMessage(MESSAGE1.getId());
    assertEquals(new MessageDTO(MESSAGE1), messageFromService);
    verify(messageRepository, times(1)).findById(MESSAGE1.getId());
  }

  @Test
  void addMessageNoTagsTest() throws IOException {
    when(messageRepository.save(any())).thenReturn(MESSAGE1);
    Message message = messageService.addMessage(USER, MESSAGE1, new ArrayList<>(),
      new MockMultipartFile("test.jpg", new FileInputStream(
      "/home/friday58/IdeaProjects/vue-blog/src/test/java/resources/test.jpg")));
    assertEquals(MESSAGE1, message);
    verify(messageRepository, times(1)).save(any());
  }

  @Test
  void addMessageTest() throws IOException {
    when(messageRepository.save(any())).thenReturn(MESSAGE1);
    Message message = messageService.addMessage(USER, MESSAGE1, Arrays.asList(TAG1, TAG2),
      new MockMultipartFile("test.jpg", new FileInputStream(
        "/home/friday58/IdeaProjects/vue-blog/src/test/java/resources/test.jpg")));
    assertEquals(MESSAGE1, message);
    assertEquals(MESSAGE1.getTags(), message.getTags());
    verify(messageRepository, times(1)).save(any());
  }

  @Test
  void deleteMessage() {
    when(messageRepository.findById(MESSAGE1.getId())).thenReturn(Optional.of(MESSAGE1));
    messageService.deleteMessage(USER, MESSAGE1.getId());
    verify(messageRepository, times(1)).findById(MESSAGE1.getId());
    verify(messageRepository, times(1)).delete(MESSAGE1);
  }

  @Test
  void likeTest() {
    MESSAGE1.setUserLikes(new HashSet<>());
    when(messageRepository.findById(MESSAGE1.getId())).thenReturn(Optional.of(MESSAGE1));
    when(messageRepository.save(MESSAGE1)).thenReturn(MESSAGE1);
    ResponseEntity<Message> response = messageService.like(USER, MESSAGE1.getId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, MESSAGE1.getUserLikes().size());
    verify(messageRepository, times(1)).findById(MESSAGE1.getId());
    verify(messageRepository, times(1)).save(MESSAGE1);
  }

  @Test
  void unlikeTest() {
    MESSAGE1.setUserLikes(new HashSet<>(Set.of(USER.getId())));
    assertEquals(1, MESSAGE1.getUserLikes().size());
    when(messageRepository.findById(MESSAGE1.getId())).thenReturn(Optional.of(MESSAGE1));
    when(messageRepository.save(MESSAGE1)).thenReturn(MESSAGE1);
    ResponseEntity<Message> response = messageService.unlike(USER, MESSAGE1.getId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(0, MESSAGE1.getUserLikes().size());
    verify(messageRepository, times(1)).findById(MESSAGE1.getId());
    verify(messageRepository, times(1)).save(MESSAGE1);
  }

  @Test
  void getUserMessagesTest() {
    Page<Message> page = new PageImpl<>(List.of(MESSAGE1));
    when(messageRepository.findAllByUserUsername(USER.getUsername(), PageRequest.of(0, 5))).thenReturn(page);
    List<MessageDTO> messagesFromService =
      messageService.getUserMessages(USER.getUsername(), PageRequest.of(0, 5));
    List<MessageDTO> messages = List.of(new MessageDTO(MESSAGE1));
    assertEquals(messages, messagesFromService);
    verify(messageRepository, times(1)).
      findAllByUserUsername(USER.getUsername(), PageRequest.of(0, 5));
  }
}
