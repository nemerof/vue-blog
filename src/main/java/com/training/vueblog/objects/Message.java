package com.training.vueblog.objects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    private String body;
    private LocalDateTime creationDate;

    //todo Remake to collections of photos
    private String photoLink;

    private String username;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_tags",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tags;

    @ElementCollection
    private Set<String> userLikes;

    public Message(String body, LocalDateTime creationDate, List<Tag> tags) {
        this.body = body;
        this.creationDate = creationDate;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", body='" + body + '\'' +
                ", tags=" + tags +
                '}';
    }
}
