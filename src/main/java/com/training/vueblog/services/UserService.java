package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final Storage storage;

    public UserService(UserRepository userRepository, PasswordEncoder encoder,
      Storage storage) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.storage = storage;
    }

    // Used for authenticating of the user
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDetails user = userRepository.getByUsername(s).orElse(null);
        if (user == null)
            user = new User();
        return user;
    }

    public ResponseEntity<User> registerUser(User user, MultipartFile file)
      throws IOException {
      User dbUser = userRepository.getByUsername(user.getUsername()).orElse(null);
      if (dbUser != null) {
        return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED); // user with such username already exists
      }

      user.setActive(true);
      user.setRoles(Collections.singleton(Role.USER));
      user.setCreationDate(LocalDateTime.now());
      user.setId(UUID.randomUUID().toString());
      user.setPassword(encoder.encode(user.getPassword()));

      if (!file.isEmpty()) {
        String link = user.getPhotoLink();
        String photoId = UUID.randomUUID() + link.substring(link.indexOf("."));
        BlobId blobId = BlobId.of("vueblog-files-bucket", photoId);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, file.getBytes());
        user.setPhotoLink("https://storage.googleapis.com/vueblog-files-bucket/" + photoId);
      }

      userRepository.save(user);

      return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public User getUser(User user) {
      if (user != null)
        System.out.println(user.getPassword());
      else
        System.out.println("No principal");
      return user;
    }

  public List<User> getUsers() {
    return userRepository.findAll();
  }

  public Set<User> getSubscriptions(User user) {
    return user.getSubscriptions();
  }
}
