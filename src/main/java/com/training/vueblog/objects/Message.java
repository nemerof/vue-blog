package com.training.vueblog.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    private String body;
    private LocalDateTime creationDate;

    //todo Remake to collections of photos
    private String photoLink;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    private List<String> tags;

    public Message(String body, LocalDateTime creationDate, List<String> tags) {
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
