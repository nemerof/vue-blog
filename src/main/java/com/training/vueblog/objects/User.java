package com.training.vueblog.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Entity
@Document(collection = "usr")
@Getter
@Setter
public class User {
    @Id
    private String id;
    private String name;
    private String userPicture;
    private String email;
    private String locale;
    private LocalDateTime lastVisit;
}
