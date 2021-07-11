package com.training.vueblog.repositories;

import com.training.vueblog.objects.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> getByUsername(String username);
}
