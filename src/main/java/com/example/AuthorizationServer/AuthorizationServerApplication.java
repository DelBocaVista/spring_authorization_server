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

import java.util.Arrays;
import java.util.Collection;
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

			Organization o2 = new Organization();
			o2.setName("STH");
			o2.setEnabled(true);

			Organization o3 = new Organization();
			o3.setName("SCI");
			o3.setEnabled(true);

			Organization o4 = new Organization();
			o4.setName("CBH");
			o4.setEnabled(true);

			Organization o5 = new Organization();
			o5.setName("Doktorander");
			o5.setEnabled(true);

			Organization o1new = o.findByName("KTH");

			orgService.addOrganization(o2);
			orgService.addOrganization(o3);
			orgService.addOrganization(o4);
			orgService.addOrganization(o5);

			orgService.addParentToOrganization(o2,o1);
			orgService.addParentToOrganization(o3,o1);
			orgService.addParentToOrganization(o4,o1);
			orgService.addParentToOrganization(o5,o2);

			Organization o2new = o.findByName("STH");

			System.out.println("test: " + o1new.getName() + " " + o1new.getPath());

			System.out.println("Chi");
			List<Organization> res = orgService.getAllChildrenOfOrganization(o1);
			for (Organization o:res) {
				System.out.println(o.getName());
			}
			System.out.println("res: " + res.get(0).getPath());

			String name = o.getName(1L);
			System.out.println("name: " + name);
		};
	}
}
