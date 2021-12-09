package com.training.vueblog;

import com.training.vueblog.objects.Role;
import com.training.vueblog.objects.User;
import com.training.vueblog.repositories.UserRepository;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VueblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(VueblogApplication.class, args);
	}

  @Bean
  public CommandLineRunner demoData(UserRepository repository, PasswordEncoder encoder) {
    return args -> {
      User admin = new User("admin_id", "admin", "123456",
        LocalDateTime.of(2021, Month.DECEMBER,3,6,30),
        LocalDateTime.now(), true, Set.of(Role.USER, Role.ADMIN));
      admin.setPassword(encoder.encode(admin.getPassword()));
      admin.setPhotoLink("https://storage.googleapis.com/vueblog-files-bucket/profile-logo.png");
      repository.save(admin);
    };
  }
}
