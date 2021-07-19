package com.training.vueblog.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    private String id;

    private String content;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tag_subscribers",
            joinColumns = {@JoinColumn(name = "tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> subscribers;

    public Tag(String tag) {
        this.id = UUID.randomUUID().toString();
        this.content = tag;
    }
}
