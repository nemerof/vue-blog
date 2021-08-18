package com.training.vueblog.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usr")
@Getter
@Setter
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "id")
// todo Implement all UserDetails interface methods
public class User implements UserDetails, Serializable {

    @Id
    private String id;

    private String username;

    private String password;

    private LocalDateTime creationDate;

    private LocalDateTime lastVisit;

    private boolean active;

    private String photoLink;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tag_subscribers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> subTags;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "user_subscriptions",
      joinColumns = {@JoinColumn(name = "channel_id")},
      inverseJoinColumns = {@JoinColumn(name = "subscriber_id")}
    )
    private Set<User> subscribers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "user_subscriptions",
      joinColumns = {@JoinColumn(name = "subscriber_id")},
      inverseJoinColumns = {@JoinColumn(name = "channel_id")}
    )
    private Set<User> subscriptions = new HashSet<>();
//
//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(
//      name = "reposts",
//      joinColumns = {@JoinColumn(name = "user_id")},
//      inverseJoinColumns = {@JoinColumn(name = "message_id")}
//    )
//    private Set<Message> reposts = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return getRoles();
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
      return roles.contains(Role.ADMIN);
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return  isActive() == user.isActive() && getId().equals(user.getId()) &&
      Objects.equals(getUsername(), user.getUsername()) &&
      Objects.equals(getRoles(), user.getRoles())
//                Objects.equals(getSubscribers(), user.getSubscribers()) &&
//                Objects.equals(getSubscriptions(), user.getSubscriptions())
      ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String toString() {
    return "User{" +
      "id='" + id + '\'' +
      ", username='" + username + '\'' +
      ", password='" + password + '\'' +
      ", creationDate=" + creationDate +
      ", lastVisit=" + lastVisit +
      ", active=" + active +
      ", photoLink='" + photoLink + '\'' +
      ", roles=" + roles +
      '}';
  }
}
