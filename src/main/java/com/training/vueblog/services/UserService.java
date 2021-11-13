package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import com.training.vueblog.objects.dto.UserDTO;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private final TagRepository tagRepository;

    private final JdbcTemplate jdbcTemplate;

    public UserService(UserRepository userRepository, PasswordEncoder encoder,
                       Storage storage, TagRepository tagRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.storage = storage;
        this.tagRepository = tagRepository;
        this.jdbcTemplate = jdbcTemplate;
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

    public Set<UserDTO> getUserSubscriptions(User user) {
      Set<UserDTO> userSubscriptions = new HashSet<>();
      for (User u : user.getSubscriptions()) {
        userSubscriptions.add(new UserDTO(u));
      }
      return userSubscriptions;
    }

    public Set<UserDTO> getUserSubscribers(User user) {
      Set<UserDTO> userSubscribers = new HashSet<>();
      for (User u : user.getSubscribers()) {
        userSubscribers.add(new UserDTO(u));
      }
      return userSubscribers;
    }

    public User getUser(User user) {
      if (user != null) {
        Set<Tag> tags = new HashSet<>();
        jdbcTemplate.query("SELECT * FROM tag_subscribers", resultSet -> {
          String userId = resultSet.getString("user_id");
          String tagId = resultSet.getString("tag_id");
          if (userId.equals(user.getId()))
            tags.add(tagRepository.findById(tagId).get());
        });
        Set<User> subscriptions = new HashSet<>();
        jdbcTemplate.query("SELECT * FROM user_subscriptions", resultSet -> {
          String subscriberId = resultSet.getString("subscriber_id");
          String channelId = resultSet.getString("subscription_id");
          if (subscriberId.equals(user.getId()))
            subscriptions.add(userRepository.findById(channelId).get());
        });
        user.setSubTags(tags);
        user.setSubscriptions(subscriptions);
      } else
        System.out.println("No principal");
      return user;
    }
//////////////////////////
    public List<UserDTO> getUsers() {
      return getUserDTOList(userRepository.findAll());
    }

    public List<UserDTO> getUserDTOList(List<User> users) {
      List<UserDTO> userDTOList = new ArrayList<>();

      for (User u : users) {
        userDTOList.add(new UserDTO(u));
      }
      return userDTOList;
    }

    public Set<UserDTO> getUserDTOSet(Set<User> users) {
      Set<UserDTO> userDTOSet = new HashSet<>();

      for (User u : users) {
        userDTOSet.add(new UserDTO(u));
      }
      return userDTOSet;
    }
/////////////////////////
    public List<UserDTO> getUsersByPattern(String inputPattern) {
      return getUserDTOList(userRepository.findAllByUsernameContains(inputPattern));
    }

    public Set<UserDTO> getSubscriptionsByPattern(String user, String inputPattern) {
      if(inputPattern.equals("")) {
        return getUserSubscriptions(userRepository.findByUsername(user));
      }
      return getUserSubscriptions(userRepository.findByUsername(user)).stream()
        .filter(p -> p.getUsername().contains(inputPattern))
        .collect(Collectors.toSet());
    }

    public Set<UserDTO> getSubscribersByPattern(String user, String inputPattern) {
      if(inputPattern.equals("")) {
        return getUserSubscribers(userRepository.findByUsername(user));
      }
      return getUserSubscribers(userRepository.findByUsername(user)).stream()
        .filter(p -> p.getUsername().contains(inputPattern))
        .collect(Collectors.toSet());
    }

    public void deleteUser(String id) {
      User user  = userRepository.findById(id).orElse(null);

      if (user != null) {
        String photoLink = user.getPhotoLink();

        if (photoLink != null) {
          System.out.println(photoLink.substring(photoLink.lastIndexOf("/") + 1));
          BlobId blobId = BlobId.of("vueblog-files-bucket", photoLink.substring(photoLink.lastIndexOf("/") + 1));
          storage.delete(blobId);
        }
        userRepository.delete(user);

      }
    }

    public User subscribe(User user, String username) {
      User dbUser = getUser(user);
      User userSub = userRepository.findByUsername(username);
      if (dbUser.getSubscriptions().contains(userSub)) {
        dbUser.getSubscriptions().remove(userSub);
        userSub.getSubscribers().remove(dbUser);
        userRepository.save(userSub);
      } else {
        dbUser.getSubscriptions().add(userSub);
      }
      userRepository.save(dbUser);
      return dbUser;
    }

    public Integer getSubscriptionsCount(String user) {
        return userRepository.findByUsername(user).getSubscriptions().size();
    }

    public Integer getSubscribersCount(String user) {
      List<User> u  = userRepository.findAll();
      return userRepository.findByUsername(user).getSubscribers().size();
    }
}
