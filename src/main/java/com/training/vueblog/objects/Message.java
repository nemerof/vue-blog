package com.training.vueblog.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
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
        return String.format(
                "Message[id=%s, body='%s', creationDate='%s']",
                id, body, creationDate);
    }
}
