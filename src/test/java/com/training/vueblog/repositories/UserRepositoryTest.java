package com.training.vueblog.repositories;

import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.ADMIN;
import static com.training.vueblog.data.UserTestData.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.training.vueblog.objects.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TagRepository tagRepository;

  @AfterEach
  public void setUp() {
    userRepository.deleteAll();
    tagRepository.deleteAll();
  }

  @BeforeEach
  public void saveTags() {
    tagRepository.save(TAG1);
    tagRepository.save(TAG2);
  }

  @Test
  public void userSaveAndFindByIdTest(){
    userRepository.save(USER);
    User userFromDb = userRepository.findById(USER.getId()).get();
    assertEquals(USER, userFromDb);
  }

  @Test
  public void userSaveAndFindByUsernameTest(){
    userRepository.save(USER);
    User userFromDb = userRepository.findByUsername(USER.getUsername());
    assertEquals(USER, userFromDb);
  }

  @Test
  public void saveAllUsersAndFindAllTest(){
    userRepository.save(USER);
    userRepository.save(ADMIN);
    List<User> usersFromDb = userRepository.findAll();
    assertEquals(Arrays.asList(USER, ADMIN), usersFromDb);
  }

  @Test
  public void saveAllUsersAndFindAllByUsernameContainsTest(){
    userRepository.save(USER);
    userRepository.save(ADMIN);
    List<User> usersFromDb = userRepository.findAllByUsernameContains(USER.getUsername());
    assertEquals(List.of(USER), usersFromDb);
  }

  @Test
  public void userSaveAndDeleteTest(){
    userRepository.save(USER);
    userRepository.delete(USER);
    User userFromDb = userRepository.findById(USER.getId()).orElse(null);
    assertNull(userFromDb);
  }
}
