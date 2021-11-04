package com.training.vueblog.objects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {

    @Id
    private String id;

    @Column(length=500)
    private String body;
    private LocalDateTime creationDate;

    //todo Remake to collections of photos
    private String photoLink;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
      name = "message_tags",
      joinColumns = {@JoinColumn(name = "message_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name="user_id")
    private Set<String> userLikes;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", username='" + user.getUsername() + '\'' +
                ", body='" + body + '\'' +
                ", tags=" + tags +
                '}';
    }
}
