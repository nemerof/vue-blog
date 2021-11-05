package com.training.vueblog.repositories;

import com.training.vueblog.objects.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Tag getByContent(String content);
    List<Tag> findAllByContentContains(String content);
}
