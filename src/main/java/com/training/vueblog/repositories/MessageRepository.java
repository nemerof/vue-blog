package com.training.vueblog.repositories;

import com.training.vueblog.objects.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findAllByBodyContains(String filter);
}
