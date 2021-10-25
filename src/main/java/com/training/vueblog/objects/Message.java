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

    private String username;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @ElementCollection()
    private List<String> tagsContent;

    @ElementCollection(fetch = FetchType.EAGER)
 //   @CollectionTable(name="message_tags", joinColumns = {@JoinColumn(name = "message_id")})
    private List<String> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> userLikes;

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
