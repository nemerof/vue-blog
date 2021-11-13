package com.training.vueblog.objects.dto;

import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.Tag;
import com.training.vueblog.objects.User;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserDTO {

  private final String id;
  private final String username;
  private final LocalDateTime lastVisit;
  private final String photoLink;
  private final Set<Role> roles;
  private final Set<String> subTags;

  public UserDTO(String id, String username, LocalDateTime lastVisit, String photoLink,
    Set<Role> roles, Set<String> subTags) {
    this.id = id;
    this.username = username;
    this.lastVisit = lastVisit;
    this.photoLink = photoLink;
    this.roles = roles;
    this.subTags = subTags;
  }

  public UserDTO(User user) {
    id = user.getId();
    username = user.getUsername();
    lastVisit = user.getLastVisit();
    photoLink = user.getPhotoLink();
    roles = user.getRoles();

    if(user.getSubTags() != null) {
      Set<String> st = new HashSet<>();
      for (Tag t : user.getSubTags())
        st.add(t.getId());
      subTags = st;
    } else subTags = null;
  }
}
