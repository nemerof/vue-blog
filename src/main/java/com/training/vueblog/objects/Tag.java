package com.training.vueblog.objects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag implements Serializable, Comparable<Tag> {

    @Id
    private String id;

    @Column(unique = true, length = 50)
    private String content;

    private int numberOfMessages;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
      name = "tag_subscribers",
      joinColumns = @JoinColumn(name = "tag_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subscribers = new HashSet<>();

  public Tag(String tag) {
    this.id = UUID.randomUUID().toString();
    this.content = tag;
  }

    public Tag(String id, String content) {
      this.id = id;
      this.content = content;
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

    @Override
    public int compareTo(Tag t) {
      return Integer.compare(t.getSubscribers().size(), this.getSubscribers().size());
    }
}
