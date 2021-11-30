package com.training.vueblog.objects;

import java.io.Serializable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority, Serializable {
  USER, ADMIN;

  @Override
  public String getAuthority() {
    return name();
  }
}
