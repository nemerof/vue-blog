package com.training.vueblog.services;

import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import java.util.Collections;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    // Used for authenticating of the user
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDetails user = repository.getByUsername(s).orElse(null);
        if (user == null)
            user = new User();
        return user;
    }

    public void createUser(User user) {
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setCreationDate(LocalDateTime.now());
        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode(user.getPassword()));

        repository.save(user);
    }
}
