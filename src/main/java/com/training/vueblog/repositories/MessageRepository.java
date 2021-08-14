package com.training.vueblog.repositories;

import com.training.vueblog.objects.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findAllByBodyContains(String filter, Pageable pageable);
    Page<Message> findAll(Pageable pageable);
    Page<Message> findAllByUserUsername(String name, Pageable pageable);
}
