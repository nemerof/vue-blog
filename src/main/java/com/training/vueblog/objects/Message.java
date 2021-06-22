package com.training.vueblog.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "message")
public class Message {

    @Id
    private String id;

    private String body;
    private LocalDateTime creationDate;

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
