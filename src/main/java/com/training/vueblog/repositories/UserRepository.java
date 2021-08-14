package com.training.vueblog.repositories;

import com.training.vueblog.objects.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> getByUsername(String username);

    List<User> findAllByUsernameContains(String username);

    User findByUsername(String username);
}
