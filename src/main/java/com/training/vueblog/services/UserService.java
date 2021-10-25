package com.training.vueblog.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.TagRepository;
import com.training.vueblog.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
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

    private Set<User> getUserSubscriptions(User user) {
      Set<User> userSubscriptions = new HashSet<>();
      for (String u : user.getSubscriptions()) {
        userSubscriptions.add(userRepository.findById(u).get());
      }
      return userSubscriptions;
    }

    private Set<User> getUserSubscribers(User user) {
      Set<User> userSubscribers = new HashSet<>();
      for (String u : user.getSubscribers()) {
        userSubscribers.add(userRepository.findById(u).get());
      }
      return userSubscribers;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public User getUser(User user) {
      if (user != null) {
        System.out.println(user.getPassword());
        Set<String> tags = new HashSet<>();
        jdbcTemplate.query("SELECT * FROM user_sub_tags", resultSet -> {
          String userId = resultSet.getString("user_id");
          String tagId = resultSet.getString("tag_id");
          if (userId.equals(user.getId()))
            tags.add(tagId);
        });
        Set<String> subscriptions = new HashSet<>();
        jdbcTemplate.query("SELECT * FROM user_subscriptions", resultSet -> {
          String subscriberId = resultSet.getString("user_id");
          String channelId = resultSet.getString("subscriptions");
          if (subscriberId.equals(user.getId()))
            subscriptions.add(channelId);
        });
        user.setSubTags(tags);
        user.setSubscriptions(subscriptions);
      } else
        System.out.println("No principal");
      return user;
    }

    public List<User> getUsers() {
      return userRepository.findAll();
    }

  //  public Set<User> getSubscriptions(String user) {
  //    return userRepository.findByUsername(user).getSubscriptions();
  //  }
  //
  //  public Set<User> getSubscribers(String user) {
  //      return userRepository.findByUsername(user).getSubscribers();
  //  }

    public List<User> getUsersByPattern(String inputPattern) {
      return userRepository.findAllByUsernameContains(inputPattern);
    }

    //real user objects
  //  public Set<User> getSubscriptionsByPattern(String user, String inputPattern) {
  //    if(inputPattern.equals("")) {
  //      User u = userRepository.findByUsername(user); ////////////////////////////////////////////////
  //      Set<User> users = userRepository.findByUsername(user).getSubscriptions();
  //      return users;
  //    }
  //    return userRepository.findByUsername(user).getSubscriptions().stream()
  //      .filter(p -> p.getUsername().contains(inputPattern))
  //      .collect(Collectors.toSet());
  //  }
  //
  //  public Set<User> getSubscribersByPattern(String user, String inputPattern) {
  //    if(inputPattern.equals("")) {
  //      return userRepository.findByUsername(user).getSubscribers();
  //    }
  //    return userRepository.findByUsername(user).getSubscribers().stream()
  //      .filter(p -> p.getUsername().contains(inputPattern))
  //      .collect(Collectors.toSet());
  //  }

    public Set<User> getSubscriptionsByPattern(String user, String inputPattern) {
      if(inputPattern.equals("")) {
        return getUserSubscriptions(userRepository.findByUsername(user));
      }
      return getUserSubscriptions(userRepository.findByUsername(user)).stream()
        .filter(p -> p.getUsername().contains(inputPattern))
        .collect(Collectors.toSet());
    }

    public Set<User> getSubscribersByPattern(String user, String inputPattern) {
      if(inputPattern.equals("")) {
        return getUserSubscribers(userRepository.findByUsername(user));
      }
      return getUserSubscribers(userRepository.findByUsername(user)).stream()
        .filter(p -> p.getUsername().contains(inputPattern))
        .collect(Collectors.toSet());
    }

  //  public Set<User> getSubscriptionsOfAnotherUser(String user) {
  //      return userRepository.findByUsername(user).getSubscriptions();
  //  }
  //
  //  public Set<User> getSubscribersOfAnotherUser(String user) {
  //    return userRepository.findByUsername(user).getSubscribers();
  //  }

  //  public List<User> getUsersExceptCurrentSubscriptions(User user, String inputPattern) {
  //    List<User> users = userRepository.findAll();
  //    users.remove(user);
  //    users.removeAll(user.getSubscriptions());
  //
  //      users = users.stream()
  //        .filter(p -> p.getUsername().contains(inputPattern))
  //        .collect(Collectors.toList());
  //
  //    return users;
  //  }

  //  public List<User> getUsersExceptCurrentSubscribers(User user, String inputPattern) {
  //    List<User> users = userRepository.findAll();
  //    users.remove(user);
  //    users.removeAll(user.getSubscribers());
  //
  //      users = users.stream()
  //        .filter(p -> p.getUsername().contains(inputPattern))
  //        .collect(Collectors.toList());
  //
  //    return users;
  //  }

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
      if (dbUser.getSubscriptions().contains(userSub.getId())) {
        dbUser.getSubscriptions().remove(userSub.getId());
        userSub.getSubscribers().remove(dbUser.getId());
      } else {
        dbUser.getSubscriptions().add(userSub.getId());
        userSub.getSubscribers().add(dbUser.getId());
      }
      userRepository.save(dbUser);
      userRepository.save(userSub);
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
