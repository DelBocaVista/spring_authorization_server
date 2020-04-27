package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repositories.OrganizationRepository;
import com.example.AuthorizationServer.services.OrganizationService;
import com.example.AuthorizationServer.services.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AuthorizationServerApplication {

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private OrganizationRepository o;

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

		Organization o1 = new Organization();
		o1.setName("KTH");
		o1.setEnabled(true);
		o1.addUser(u1);

		u1.setOrganization(o1);

		return () -> {
			orgService.addOrganization(o1);
			userService.addUser(u1);
			userService.addUser(u2);
			userService.addUser(u3);
			//orgService.addOrganization(o1);
			Organization test = o.findByName("KTH");
			System.out.println("test: " + test.getName());

			List<Organization> res = o.findAll();
			System.out.println("res: " + res.get(0).getPath());

			String name = o.getName(1L);
			System.out.println("name: " + name);
		};
	}
}
