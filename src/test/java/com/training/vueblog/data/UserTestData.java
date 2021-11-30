package com.training.vueblog.data;

import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import java.time.LocalDateTime;
import java.util.EnumSet;

public class UserTestData {

  public static final User ADMIN = new User(
    "e3bc2f54-6424-4716-be1a-0fe2cf61f05b", "user", "user123",
      LocalDateTime.of(2021,10,2,21,13,23,384912),
      LocalDateTime.of(2021,11,4,23,27,51,273394),
      true, EnumSet.of(Role.ADMIN, Role.USER));

  public static final User USER = new User(
    "7831164a-34c9-43f0-856f-4f1af8317def", "admin", "admin123",
      LocalDateTime.of(2021,8,12,19,34,22,749346),
      LocalDateTime.of(2021,11,5,10,1,53,846374),
      true, EnumSet.of(Role.USER));
}
