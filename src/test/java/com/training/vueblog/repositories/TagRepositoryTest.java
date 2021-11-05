package com.training.vueblog.repositories;

import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.training.vueblog.objects.Tag;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TagRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @AfterEach
  public void deleteAll() {
    tagRepository.deleteAll();
  }

  @Test
  public void tagSaveAndFindByIdTest(){
    tagRepository.save(TAG1);
    Tag tagFromDb = tagRepository.findById(TAG1.getId()).get();
    assertEquals(TAG1, tagFromDb);
  }

  @Test
  public void tagSaveAndGetByContentTest(){
    tagRepository.save(TAG1);
    Tag tagFromDb = tagRepository.getByContent(TAG1.getContent());
    assertEquals(TAG1, tagFromDb);
  }

  @Test
  public void saveAllTagsAndFindAllTest(){
    tagRepository.save(TAG1);
    tagRepository.save(TAG2);
    List<Tag> tagsFromDb = tagRepository.findAll();
    assertEquals(Arrays.asList(TAG1, TAG2), tagsFromDb);
  }

  @Test
  public void saveAllTagsAndFindAllByContentContainsTest(){
    tagRepository.save(TAG1);
    tagRepository.save(TAG2);
    List<Tag> tagsFromDb = tagRepository.findAllByContentContains(TAG1.getContent());
    assertEquals(Arrays.asList(TAG1), tagsFromDb);
  }

  @Test
  public void tagSaveAndDeletedTest(){
    tagRepository.save(TAG1);
    tagRepository.delete(TAG1);
    Tag tagFromDb = tagRepository.findById(TAG1.getId()).orElse(null);
    assertNull(tagFromDb);
  }
}
