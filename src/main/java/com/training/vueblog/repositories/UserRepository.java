package com.training.vueblog.repositories;

import com.training.vueblog.objects.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
