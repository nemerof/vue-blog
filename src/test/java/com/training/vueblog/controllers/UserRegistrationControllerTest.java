package com.training.vueblog.controllers;

import static com.training.vueblog.controllers.JsonConverter.objectToJsonString;
import static com.training.vueblog.data.UserTestData.USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.training.vueblog.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationControllerTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private UserRegistrationController userRegistrationController;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController)
      .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver())
      .build();
  }

  @Test
  public void registerUser() throws Exception {
    MockMultipartFile multipartFile =
      new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "plug".getBytes());
    when(userService.registerUser(USER, multipartFile)).
      thenReturn(new ResponseEntity<>(USER, HttpStatus.OK));

    mockMvc.perform(multipart("/api/user").
        file(new MockMultipartFile("user", "",
          "application/json", objectToJsonString(USER).getBytes())).
        file(multipartFile)).
      andExpect(status().isOk());
    verify(userService, times(1)).registerUser(USER, multipartFile);
  }

  @Test
  public void getUserTest() throws Exception {
    when(userService.getUser(USER)).thenReturn(USER);

    String responseJson = objectToJsonString(USER);

    mockMvc.perform(get("/api/user"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(userService, times(1)).getUser(USER);
  }
}
