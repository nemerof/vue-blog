package com.training.vueblog.repositories;

import com.training.vueblog.objects.Message;
import com.training.vueblog.objects.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findAllByBodyContains(String filter, Pageable pageable);

    List<Message> findAllByBodyContains(String filter);
    List<Message> findAllByTagsContains(Tag tag);

    Page<Message> findAll(Pageable pageable);
    Page<Message> findAllByUserUsername(String name, Pageable pageable);

    List<Message> findAllByUserUsername(String name);
}
