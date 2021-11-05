package com.training.vueblog.repositories;


import static com.training.vueblog.data.MessageTestData.MESSAGE1;
import static com.training.vueblog.data.MessageTestData.MESSAGE2;
import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.ADMIN;
import static com.training.vueblog.data.UserTestData.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.training.vueblog.objects.Message;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TagRepository tagRepository;

  @BeforeEach
  public void addUsers() {
    userRepository.save(USER);
    userRepository.save(ADMIN);
    tagRepository.save(TAG1);
    tagRepository.save(TAG2);
  }

  @AfterEach
  public void deleteAll() {
    tagRepository.deleteAll();
    messageRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void messageSaveAndFindByIdTest(){
    messageRepository.save(MESSAGE1);
    Message messageFromDb = messageRepository.findById(MESSAGE1.getId()).get();
    assertEquals(MESSAGE1, messageFromDb);
  }

  @Test
  public void saveAllMessagesAndFindAllTest(){
    messageRepository.save(MESSAGE1);
    messageRepository.save(MESSAGE2);
    List<Message> messagesFromDb = messageRepository.findAll(PageRequest.of(0, 5)).
      stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(MESSAGE1, MESSAGE2), messagesFromDb);
  }

  @Test
  public void saveAllMessagesAndFindAllByBodyContainsTest(){
    messageRepository.save(MESSAGE1);
    messageRepository.save(MESSAGE2);
    List<Message> messagesFromDb = messageRepository.findAllByBodyContains("Body 1", PageRequest.of(0, 5)).
      stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(MESSAGE1), messagesFromDb);
  }

  @Test
  public void saveAllMessagesAndFindAllByUserUsernameTest(){
    messageRepository.save(MESSAGE1);
    messageRepository.save(MESSAGE2);
    List<Message> messagesFromDb = messageRepository.findAllByUserUsername("user", PageRequest.of(0, 5)).
      stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(MESSAGE2), messagesFromDb);
  }

  @Test
  public void messageSaveAndDeleteTest(){
    messageRepository.save(MESSAGE1);
    messageRepository.delete(MESSAGE1);
    Message messageFromDb = messageRepository.findById(MESSAGE1.getId()).orElse(null);
    assertNull(messageFromDb);
  }
}
