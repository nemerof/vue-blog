package com.training.vueblog.repositories;

import com.training.vueblog.objects.SimpleMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<SimpleMessage, String> {

    public SimpleMessage findByHeader(String header);
}
