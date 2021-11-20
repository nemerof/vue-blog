package com.training.vueblog.controllers;

import static com.training.vueblog.controllers.JsonConverter.objectToJsonString;
import static com.training.vueblog.data.MessageTestData.MESSAGE1;
import static com.training.vueblog.data.MessageTestData.MESSAGE2;
import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.services.MessageService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
//@EnableSpringDataWebSupport
public class MessageControllerTest {

  @Mock
  private Storage storage;
  @Mock
  private MessageService messageService;
  @InjectMocks
  private MessageController messageController;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(messageController)
      .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
        new PrincipalDetailsArgumentResolver())
      .build();
  }

  @Test
  public void getAllMessagesNoFilterNotByTagTest() throws Exception {
    List<MessageDTO> messageDTOS = Arrays.asList(new MessageDTO(MESSAGE1), new MessageDTO(MESSAGE2));
    when(messageService.getAllMessages("", false,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()))).thenReturn(messageDTOS);

    String responseJson =
      objectToJsonString(Arrays.asList(new MessageDTO(MESSAGE1), new MessageDTO(MESSAGE2)));

    mockMvc.perform(get("/api/message?filter=&bytag=false&page=0"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(messageService, times(1)).getAllMessages("", false,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()));
  }

  @Test
  public void getAllMessagesFilterNotByTagTest() throws Exception {
    List<MessageDTO> messageDTOS = List.of(new MessageDTO(MESSAGE1));
    when(messageService.getAllMessages("Body 1", false,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()))).thenReturn(messageDTOS);

    String responseJson =
      objectToJsonString(List.of(new MessageDTO(MESSAGE1)));

    mockMvc.perform(get("/api/message?filter=Body 1&bytag=false&page=0"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(messageService, times(1)).getAllMessages("Body 1", false,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()));
  }

  @Test
  public void getAllMessagesFilterByTagTest() throws Exception {
    List<MessageDTO> messageDTOS = List.of(new MessageDTO(MESSAGE1));
    when(messageService.getAllMessages("tag1", true,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()))).thenReturn(messageDTOS);

    String responseJson =
      objectToJsonString(List.of(new MessageDTO(MESSAGE1)));

    mockMvc.perform(get("/api/message?filter=tag1&bytag=true&page=0"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(messageService, times(1)).getAllMessages("tag1", true,
      PageRequest.of(0, 5, Sort.by("creationDate").descending()));
  }

  @Test
  public void getUserMessagesTest() throws Exception {
    List<MessageDTO> messageDTOS = List.of(new MessageDTO(MESSAGE1));
    when(messageService.getUserMessages("user",
      PageRequest.of(0, 5, Sort.by("creationDate").descending()))).thenReturn(messageDTOS);

    String responseJson = objectToJsonString(List.of(new MessageDTO(MESSAGE1)));

    mockMvc.perform(get("/api/message/user/user"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(messageService, times(1)).getUserMessages("user",
      PageRequest.of(0, 5, Sort.by("creationDate").descending()));
  }

  @Test
  public void getMessageTest() throws Exception {
    MessageDTO messageDTO = new MessageDTO(MESSAGE1);
    when(messageService.getMessage(MESSAGE1.getId())).thenReturn(messageDTO);

    String responseJson = objectToJsonString(messageDTO);

    mockMvc.perform(get("/api/message/" + MESSAGE1.getId()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(messageService, times(1)).getMessage(MESSAGE1.getId());
  }


  @Test
  public void addMessageTest() throws Exception {
    MockMultipartFile multipartFile =
      new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "plug".getBytes());

    when(messageService.addMessage(eq(USER), any(), eq(Arrays.asList(TAG1, TAG2)), eq(multipartFile))).
      thenReturn(MESSAGE1);

    mockMvc.perform(multipart("/api/message/add").
        file(new MockMultipartFile("text", "",
        "application/json", objectToJsonString(new MessageDTO(MESSAGE1)).getBytes())).
        file(new MockMultipartFile("tags", "",
          "application/json", objectToJsonString(Arrays.asList(TAG1, TAG2)).getBytes())).
        file(multipartFile)).
        andExpect(status().isOk());
    verify(messageService, times(1)).
      addMessage(eq(USER), any(), eq(Arrays.asList(TAG1, TAG2)), eq(multipartFile));
  }

  @Test
  public void deleteMessageTest() throws Exception {
    doNothing().when(messageService).deleteMessage(USER, MESSAGE1.getId());
    mockMvc.perform(delete("/api/message/" + MESSAGE1.getId()))
        .andExpect(status().isOk());
    verify(messageService, times(1)).deleteMessage(USER, MESSAGE1.getId());
  }

  @Test
  public void likeTest() throws Exception {
    when(messageService.like(USER, MESSAGE1.getId())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    mockMvc.perform(get("/api/message/like/" + MESSAGE1.getId()))
      .andExpect(status().isOk());
    verify(messageService, times(1)).like(USER, MESSAGE1.getId());
  }

  @Test
  public void unlikeTest() throws Exception {
    when(messageService.unlike(USER, MESSAGE1.getId())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    mockMvc.perform(get("/api/message/unlike/" + MESSAGE1.getId()))
      .andExpect(status().isOk());
    verify(messageService, times(1)).unlike(USER, MESSAGE1.getId());
  }
}
