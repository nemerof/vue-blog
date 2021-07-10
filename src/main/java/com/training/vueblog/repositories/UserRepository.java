package com.training.vueblog.repositories;

import com.training.vueblog.objects.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
