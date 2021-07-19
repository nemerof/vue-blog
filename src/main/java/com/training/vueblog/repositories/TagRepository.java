package com.training.vueblog.repositories;

import com.training.vueblog.objects.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag getByContent(String content);
}
