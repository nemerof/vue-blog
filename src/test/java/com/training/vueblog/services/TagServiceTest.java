package com.training.vueblog.services;

import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.dto.TagDTO;
import com.training.vueblog.repositories.MessageRepository;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

  @Mock
  private TagRepository tagRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageService messageService;
  @Mock
  private UserService userService;
  @Mock
  private Storage storage;
  @InjectMocks
  private TagService tagService;

//  @Test
//  void subToTagTest() {
//
//  }

  @Test
  void getPopularTagsTest() {
    when(tagRepository.findAll()).thenReturn(Arrays.asList(TAG1, TAG2));
    List<TagDTO> popularTagsFromService = tagService.getPopularTags();
    List<TagDTO> popularTags = Arrays.asList(new TagDTO(TAG1), new TagDTO(TAG2));
    assertEquals(popularTags, popularTagsFromService);
    verify(tagRepository, times(1)).findAll();
  }

  @Test
  void getTagDTOListTest() {
    List<TagDTO> tagDTOListFromService = tagService.getTagDTOList(Arrays.asList(TAG1, TAG2));
    List<TagDTO> tagDTOList = Arrays.asList(new TagDTO(TAG1), new TagDTO(TAG2));
    assertEquals(tagDTOList, tagDTOListFromService);
  }

  @Test
  void getPopularTagsByPatternTest() {
    when(tagRepository.findAllByContentContains("tag1")).thenReturn(List.of(TAG1));
    List<TagDTO> tagDTOListFromServiceByPattern = tagService.getPopularTagsByPattern("tag1");
    List<TagDTO> tagDTOListByPattern = List.of(new TagDTO(TAG1));
    assertEquals(tagDTOListByPattern, tagDTOListFromServiceByPattern);
    verify(tagRepository, times(1)).findAllByContentContains("tag1");
  }

  @Test
  void getTagTest() {
    when(tagRepository.getByContent("tag1")).thenReturn(TAG1);
    assertEquals(new TagDTO(TAG1), tagService.getTag("tag1"));
    verify(tagRepository, times(1)).getByContent("tag1");
  }
}
