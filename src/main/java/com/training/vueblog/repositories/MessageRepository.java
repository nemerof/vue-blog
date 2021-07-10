package com.training.vueblog.repositories;

import com.training.vueblog.objects.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {

}
