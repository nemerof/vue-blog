package com.training.vueblog.services;

import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.ADMIN;
import static com.training.vueblog.data.UserTestData.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.UserDTO;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder encoder;
  @Mock
  private Storage storage;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private UserService userService;

  @BeforeAll
  static void addSubs() {
    ADMIN.setSubscriptions(Set.of(USER));
    ADMIN.setSubscribers(Set.of(USER));
    USER.setSubscriptions(Set.of(ADMIN));
    USER.setSubscribers(Set.of(ADMIN));

    USER.setSubTags(Set.of(TAG1, TAG2));
    ADMIN.setSubTags(Set.of(TAG1));
    TAG1.setSubscribers(Set.of(USER, ADMIN));
    TAG2.setSubscribers(Set.of(USER));
  }

  @AfterAll
  static void deleteSubs() {
    ADMIN.setSubscriptions(null);
    ADMIN.setSubscribers(null);
    USER.setSubscriptions(null);
    USER.setSubscribers(null);

    USER.setSubTags(null);
    ADMIN.setSubTags(null);
    TAG1.setSubscribers(null);
    TAG2.setSubscribers(null);
  }


  @Test
  void loadByUsername() {
    when(userRepository.getByUsername(USER.getUsername())).thenReturn(Optional.of(USER));
    assertEquals(USER, userService.loadUserByUsername(USER.getUsername()));
    verify(userRepository, times(1)).getByUsername(USER.getUsername());
  }

  @Test
  void registerUserSuccessfulTest() throws IOException {
    USER.setPhotoLink("test.jpg");
    when(userRepository.getByUsername(USER.getUsername())).thenReturn(Optional.empty());
    when(userRepository.save(any())).thenReturn(USER);
    ResponseEntity<User> responseFromService = userService.registerUser(USER,
      new MockMultipartFile("test.jpg", new FileInputStream(
        "/home/friday58/IdeaProjects/vue-blog/src/test/java/resources/test.jpg")));
    assertEquals(USER, responseFromService.getBody());
    assertEquals(HttpStatus.OK, responseFromService.getStatusCode());
    verify(userRepository, times(1)).getByUsername(USER.getUsername());
    verify(userRepository,times(1)).save(any());
  }

  @Test
  void registerUserUnsuccessfulTest() throws IOException {
    when(userRepository.getByUsername(USER.getUsername())).thenReturn(Optional.of(USER));
    ResponseEntity<User> responseFromService = userService.registerUser(USER,
      new MockMultipartFile("test.jpg", new FileInputStream(
        "/home/friday58/IdeaProjects/vue-blog/src/test/java/resources/test.jpg")));
    assertEquals(HttpStatus.ALREADY_REPORTED, responseFromService.getStatusCode());
    verify(userRepository, times(1)).getByUsername(USER.getUsername());
  }

  @Test
  void getUserDTOListTest() {
    List<UserDTO> userDTOListFromService = userService.getUserDTOList(Arrays.asList(USER, ADMIN));
    List<UserDTO> userDTOList = Arrays.asList(new UserDTO(USER), new UserDTO(ADMIN));
    assertEquals(userDTOList, userDTOListFromService);
  }

  @Test
  void getUsersTest() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(USER, ADMIN));
    List<UserDTO> allUsersFromService = userService.getUsers();
    List<UserDTO> userDTOList = userService.getUserDTOList(Arrays.asList(USER, ADMIN));
    assertEquals(userDTOList, allUsersFromService);
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void getUsersByPattern() {
    when(userRepository.findAllByUsernameContains(USER.getUsername())).thenReturn(List.of(USER));
    List<UserDTO> usersByPatternFromService = userService.getUsersByPattern(USER.getUsername());
    List<UserDTO> userDTOList = userService.getUserDTOList(List.of(USER));
    assertEquals(userDTOList, usersByPatternFromService);
    verify(userRepository, times(1)).findAllByUsernameContains(USER.getUsername());
  }

  @Test
  void getUserSubscriptionsTest() {
    Set<UserDTO> userSubscriptions = Set.of(new UserDTO(ADMIN));
    Set<UserDTO> userSubscriptionsFromService = userService.getUserSubscriptions(USER);
    assertEquals(userSubscriptions, userSubscriptionsFromService);
  }

  @Test
  void getUserSubscribersTest() {
    Set<UserDTO> userSubscribers = Set.of(new UserDTO(ADMIN));
    Set<UserDTO> userSubscribersFromService = userService.getUserSubscribers(USER);
    assertEquals(userSubscribers, userSubscribersFromService);
  }

  @Test
  void getSubscriptionsByPatternTest() {
    when(userRepository.findByUsername(USER.getUsername())).thenReturn(USER);
    Set<UserDTO> userSubscriptions = Set.of(new UserDTO(ADMIN));
    Set<UserDTO> userSubscriptionsFromService = userService.getSubscriptionsByPattern(USER.getUsername(), ADMIN.getUsername());
    assertEquals(userSubscriptions, userSubscriptionsFromService);
    verify(userRepository, times(1)).findByUsername(USER.getUsername());
  }

  @Test
  void getSubscribersByPatternTest() {
    when(userRepository.findByUsername(USER.getUsername())).thenReturn(USER);
    Set<UserDTO> userSubscribers = Set.of(new UserDTO(ADMIN));
    Set<UserDTO> userSubscribersFromService = userService.getSubscribersByPattern(USER.getUsername(), ADMIN.getUsername());
    assertEquals(userSubscribers, userSubscribersFromService);
    verify(userRepository, times(1)).findByUsername(USER.getUsername());
  }

  @Test
  void deleteUserTest() {
    when(userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
    userService.deleteUser(USER.getId());
    verify(userRepository, times(1)).findById(USER.getId());
    verify(userRepository, times(1)).delete(USER);
  }

  @Test
  void getSubscriptionsCountTest() {
    when(userRepository.findByUsername(USER.getUsername())).thenReturn(USER);
    int actualNumber = USER.getSubscriptions().size();
    int resultFromService = userService.getSubscriptionsCount(USER.getUsername());
    assertEquals(actualNumber, resultFromService);
    verify(userRepository, times(1)).findByUsername(USER.getUsername());
  }

  @Test
  void getSubscribersCountTest() {
    when(userRepository.findByUsername(USER.getUsername())).thenReturn(USER);
    int actualNumber = USER.getSubscribers().size();
    int resultFromService = userService.getSubscribersCount(USER.getUsername());
    assertEquals(actualNumber, resultFromService);
    verify(userRepository, times(1)).findByUsername(USER.getUsername());
  }
}
