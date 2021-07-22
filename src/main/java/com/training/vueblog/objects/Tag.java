package com.training.vueblog.objects;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag implements Serializable {

    @Id
    private String id;

    @Column(unique = true)
    private String content;

    private int numberOfMessages;

    public Tag(String tag) {
        this.id = UUID.randomUUID().toString();
        this.content = tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(content, tag.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }
}
