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
		u1.setUsername("sudo");
		u1.setFirstname("Sudo");
		u1.setLastname("McSudo");
		u1.setRole("SUPERADMIN");
		u1.setEnabled(true);
		u1.setPassword("sudo");

		UserEntity u2 = new UserEntity();
		u2.setUsername("admin");
		u2.setFirstname("Admin");
		u2.setLastname("McAdmin");
		u2.setRole("ADMIN");
		u2.setEnabled(true);
		u2.setPassword("admin");

		UserEntity u3 = new UserEntity();
		u3.setUsername("user");
		u3.setFirstname("User");
		u3.setLastname("McUser");
		u3.setRole("USER");
		u3.setEnabled(true);
		u3.setPassword("user");

		return () -> {
			userService.addUser(u1);
			userService.addUser(u2);
			userService.addUser(u3);
		};
	}
}
