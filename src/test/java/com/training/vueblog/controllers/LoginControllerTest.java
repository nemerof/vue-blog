package com.training.vueblog.controllers;

import static com.training.vueblog.controllers.JsonConverter.objectToJsonString;
import static com.training.vueblog.data.UserTestData.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.training.vueblog.services.LoginService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

  @Mock
  private LoginService loginService;
  @InjectMocks
  private LoginController loginController;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
  }

  @Test
  public void authorizeTest() throws Exception{
    when(loginService.authorize(eq(USER), any())).
      thenReturn(new ResponseEntity<>(HttpStatus.OK));

    mockMvc.perform(post("/api/login").
        contentType(MediaType.APPLICATION_JSON).
        content(objectToJsonString(USER))).
      andExpect(status().isOk());

    verify(loginService, times(1)).
      authorize(eq(USER), any());
  }

}
