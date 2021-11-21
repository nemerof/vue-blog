package com.training.vueblog.controllers;

import static com.training.vueblog.controllers.JsonConverter.objectToJsonString;
import static com.training.vueblog.data.MessageTestData.MESSAGE1;
import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.training.vueblog.objects.dto.MessageDTO;
import com.training.vueblog.objects.dto.TagDTO;
import com.training.vueblog.services.TagService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class TagControllerTest {

  @Mock
  private TagService tagService;
  @InjectMocks
  private TagController tagController;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(tagController)
      .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
        new PrincipalDetailsArgumentResolver())
      .build();
  }

  @Test
  public void subToTagTest() throws Exception {
    when(tagService.subToTag(USER, TAG1.getContent())).thenReturn(USER);
    mockMvc.perform(get("/api/tags?tag=" + TAG1.getContent()))
      .andExpect(status().isOk());
    verify(tagService, times(1)).subToTag(USER, TAG1.getContent());
  }

  @Test
  public void getPopularTagsTest() throws Exception {
    List<TagDTO> tagDTOS = Arrays.asList(new TagDTO(TAG1), new TagDTO(TAG2));
    when(tagService.getPopularTags()).thenReturn(tagDTOS);

    String responseJson = objectToJsonString(tagDTOS);

    mockMvc.perform(get("/api/tags/popular"))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(tagService, times(1)).getPopularTags();
  }

  @Test
  public void getPopularTagsByPatternTest() throws Exception {
    List<TagDTO> tagDTOS = List.of(new TagDTO(TAG1));
    when(tagService.getPopularTagsByPattern("tag1")).thenReturn(tagDTOS);

    String responseJson = objectToJsonString(tagDTOS);

    mockMvc.perform(get("/api/tags/popular/" + TAG1.getContent()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(tagService, times(1)).getPopularTagsByPattern("tag1");
  }

  @Test
  public void getTagTest() throws Exception {
    TagDTO tagDTO = new TagDTO(TAG1);
    when(tagService.getTag(TAG1.getContent())).thenReturn(tagDTO);

    String responseJson = objectToJsonString(tagDTO);

    mockMvc.perform(get("/api/tags/" + TAG1.getContent()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(tagService, times(1)).getTag(TAG1.getContent());
  }

  @Test
  public void getTagMessagesTest() throws Exception {
    List<MessageDTO> messageDTOS = List.of(new MessageDTO(MESSAGE1));
    when(tagService.getTagMessages(TAG1.getContent(),
      PageRequest.of(0, 5, Sort.by("creationDate").descending()))).thenReturn(messageDTOS);

    String responseJson = objectToJsonString(messageDTOS);

    mockMvc.perform(get("/api/tags/messages/" + TAG1.getContent()))
      .andExpect(status().isOk())
      .andExpect(content().json(responseJson));
    verify(tagService, times(1)).getTagMessages(TAG1.getContent(),
      PageRequest.of(0, 5, Sort.by("creationDate").descending()));
  }
}
