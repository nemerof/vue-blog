package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    private final Storage storage;

    public UserService(UserRepository repository, PasswordEncoder encoder,
      Storage storage) {
        this.repository = repository;
        this.encoder = encoder;
        this.storage = storage;
    }

    // Used for authenticating of the user
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDetails user = repository.getByUsername(s).orElse(null);
        if (user == null)
            user = new User();
        return user;
    }

    public void createUser(User user, MultipartFile file) throws IOException {
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setCreationDate(LocalDateTime.now());
        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode(user.getPassword()));

        if (!file.isEmpty()) {
          String link = user.getPhotoLink();
          String photoId = UUID.randomUUID().toString() + link.substring(link.indexOf("."));
          BlobId blobId = BlobId.of("vueblog-files-bucket", photoId);
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
          storage.create(blobInfo, file.getBytes());
          user.setPhotoLink("https://storage.googleapis.com/vueblog-files-bucket/" + photoId);
        }

        repository.save(user);
    }
}
