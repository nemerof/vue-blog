package com.training.vueblog.controllers;

import static com.training.vueblog.controllers.JsonConverter.objectToJsonString;
import static com.training.vueblog.data.UserTestData.ADMIN;
import static com.training.vueblog.data.UserTestData.USER;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.training.vueblog.objects.dto.UserDTO;
import com.training.vueblog.services.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private UserController userController;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
      .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver())
      .build();
  }

  @Test
  public void getUsersTest() throws Exception {
    List<UserDTO> userDTOS = Arrays.asList(new UserDTO(USER), new UserDTO(ADMIN));
    when(userService.getUsers()).thenReturn(userDTOS);

    String responseJson = objectToJsonString(userDTOS);

    mockMvc.perform(get("/api/users"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).getUsers();
  }

  @Test
  public void deleteUser() throws Exception {
    doNothing().when(userService).deleteUser(ADMIN.getId(), USER);
    mockMvc.perform(delete("/api/users/" + ADMIN.getId()))
      .andExpect(status().isOk());
    verify(userService, times(1)).deleteUser(ADMIN.getId(), USER);
  }

  @Test
  public void getUsersByPatternTest() throws Exception {
    List<UserDTO> userDTOS = List.of(new UserDTO(USER));
    when(userService.getUsersByPattern(USER.getUsername())).thenReturn(userDTOS);

    String responseJson = objectToJsonString(userDTOS);

    mockMvc.perform(get("/api/users/" + USER.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).getUsersByPattern(USER.getUsername());
  }

  @Test
  public void subscribeTest() throws Exception {
    when(userService.subscribe(USER, ADMIN.getUsername())).thenReturn(USER);

    String responseJson = objectToJsonString(USER);

    mockMvc.perform(get("/api/subscribe?username=" + ADMIN.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).subscribe(USER, ADMIN.getUsername());
  }

  @Test
  public void getSubscribersByPatternTest() throws Exception {
    Set<UserDTO> userDTOS = Set.of(new UserDTO(ADMIN));
    when(userService.getSubscribersByPattern(USER.getUsername(), ADMIN.getUsername())).thenReturn(userDTOS);

    String responseJson = objectToJsonString(userDTOS);

    mockMvc.perform(
      get("/api/subscribers/" + USER.getUsername() + "?inputPattern=" + ADMIN.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).
      getSubscribersByPattern(USER.getUsername(), ADMIN.getUsername());
  }

  @Test
  public void getSubscriptionsByPatternTest() throws Exception {
    Set<UserDTO> userDTOS = Set.of(new UserDTO(ADMIN));
    when(userService.getSubscriptionsByPattern(USER.getUsername(), ADMIN.getUsername())).thenReturn(userDTOS);

    String responseJson = objectToJsonString(userDTOS);

    mockMvc.perform(
        get("/api/subscriptions/" + USER.getUsername() + "?inputPattern=" + ADMIN.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).
      getSubscriptionsByPattern(USER.getUsername(), ADMIN.getUsername());
  }

  @Test
  public void getSubscriptionsCountTest() throws Exception {
    when(userService.getSubscriptionsCount(USER.getUsername())).thenReturn(1);

    String responseJson = objectToJsonString(1);

    mockMvc.perform(
        get("/api/subscriptions-count/" + USER.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).getSubscriptionsCount(USER.getUsername());
  }

  @Test
  public void getSubscribersCountTest() throws Exception {
    when(userService.getSubscribersCount(USER.getUsername())).thenReturn(1);

    String responseJson = objectToJsonString(1);

    mockMvc.perform(
        get("/api/subscribers-count/" + USER.getUsername()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).getSubscribersCount(USER.getUsername());
  }
}
