package com.training.vueblog.objects;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Document(collection = "message")
public class SimpleMessage {

    @Id
    public String id;

    public String header;
    public String data;

    @Override
    public String toString() {
        return String.format(
                "Message[id=%s, header='%s', data='%s']",
                id, header, data);
    }
}
