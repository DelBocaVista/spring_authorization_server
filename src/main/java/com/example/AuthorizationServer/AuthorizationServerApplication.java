package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.services.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuthorizationServerApplication {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	@Bean
	InitializingBean seedDatabase() {

		UserEntity u1 = new UserEntity();
		u1.setUsername("superadmin");
		u1.setRole("ADMIN");
		u1.setEnabled(true);
		u1.setPassword("test");

		UserEntity u2 = new UserEntity();
		u2.setUsername("jonas");
		u2.setRole("USER");
		u2.setEnabled(true);
		u2.setPassword("jonas");

		return () -> {
			userService.addUser(u1);
			userService.addUser(u2);
		};
	}
}
