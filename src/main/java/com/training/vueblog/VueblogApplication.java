package com.training.vueblog;

import com.training.vueblog.objects.SimpleMessage;
import com.training.vueblog.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class VueblogApplication {

	@Autowired
	private MessageRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(VueblogApplication.class, args);
	}
}
