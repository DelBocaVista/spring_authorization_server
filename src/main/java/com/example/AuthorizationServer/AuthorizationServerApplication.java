package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repository.UserEntityRepository;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * The Application class containing the runnable main. Also responsible for seeding the database.
 */
@SpringBootApplication
public class AuthorizationServerApplication {

	@Autowired
	private UserService userService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/*
	@Bean
	InitializingBean seedDatabase() {

		// Adding super admin user "sudo" to database if does not already exist (see comment below for an example of how
		// a more extensive seeding of the database could be performed).
		return () -> {
			UserEntity sudo = new UserEntity();
			sudo.setUsername("sudo");
			sudo.setFirstname("Sudo");
			sudo.setLastname("McSudo");
			sudo.setRole("SUPERADMIN");
			sudo.setEnabled(true);
			sudo.setPassword("sudo");

			userService.addUser(sudo);
		};
	}*/

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	// Example of extensive seeding of the database.

	@Autowired
	private UserEntityRepository userRep;

	@Autowired
	private OrganizationService orgService;

	@Bean
	InitializingBean seedDatabase() {
		return () -> {
			// Organizations
			Organization o1 = new Organization();
			o1.setName("KTH");
			o1.setEnabled(true);

			Organization o2 = new Organization();
			o2.setName("STH");
			o2.setEnabled(true);

			Organization o3 = new Organization();
			o3.setName("SCI");
			o3.setEnabled(true);

			Organization o4 = new Organization();
			o4.setName("Teachers");
			o4.setEnabled(true);

			Organization o5 = new Organization();
			o5.setName("Students");
			o5.setEnabled(true);

			Organization o6 = new Organization();
			o6.setName("SU");
			o6.setEnabled(true);

			Organization o7 = new Organization();
			o7.setName("Engelska institutionen");
			o7.setEnabled(true);

			Organization o8 = new Organization();
			o8.setName("Historiska institutionen");
			o8.setEnabled(true);

			orgService.addOrganizationSeed(o1);
			orgService.addOrganizationSeed(o2);
			orgService.addOrganizationSeed(o3);
			orgService.addOrganizationSeed(o4);
			orgService.addOrganizationSeed(o5);
			orgService.addOrganizationSeed(o6);
			orgService.addOrganizationSeed(o7);
			orgService.addOrganizationSeed(o8);

			orgService.addParentToOrganization(o2,o1);
			orgService.addParentToOrganization(o3,o1);
			orgService.addParentToOrganization(o4,o2);
			orgService.addParentToOrganization(o5,o2);
			orgService.addParentToOrganization(o7,o6);
			orgService.addParentToOrganization(o8,o6);

			// Users
			UserEntity u1 = new UserEntity();
			u1.setUsername("sudo");
			u1.setFirstname("Sudo");
			u1.setLastname("McSudo");
			u1.setRole("SUPERADMIN");
			u1.setEnabled(true);
			u1.setPassword("sudo");

			UserEntity u2 = new UserEntity();
			u2.setUsername("kthadmin");
			u2.setFirstname("Admin");
			u2.setLastname("McAdmin");
			u2.setRole("ADMIN");
			u2.setEnabled(true);
			u2.setPassword("kthadmin");

			UserEntity u3 = new UserEntity();
			u3.setUsername("suadmin");
			u3.setFirstname("Admin");
			u3.setLastname("McAdmin");
			u3.setRole("ADMIN");
			u3.setEnabled(true);
			u3.setPassword("suadmin");

			UserEntity u4 = new UserEntity();
			u4.setUsername("kthuser");
			u4.setFirstname("User");
			u4.setLastname("McUser");
			u4.setRole("USER");
			u4.setEnabled(true);
			u4.setPassword("kthuser");

			UserEntity u5 = new UserEntity();
			u5.setUsername("jonas");
			u5.setFirstname("Jonas");
			u5.setLastname("McJonas");
			u5.setRole("USER");
			u5.setEnabled(true);
			u5.setPassword("jonas");

			UserEntity u6 = new UserEntity();
			u6.setUsername("gustav");
			u6.setFirstname("Gustav");
			u6.setLastname("McGustav");
			u6.setRole("USER");
			u6.setEnabled(true);
			u6.setPassword("gustav");

			UserEntity u7 = new UserEntity();
			u7.setUsername("andreas");
			u7.setFirstname("Andreas");
			u7.setLastname("McAndreas");
			u7.setRole("USER");
			u7.setEnabled(true);
			u7.setPassword("andreas");

			UserEntity u8 = new UserEntity();
			u8.setUsername("erik");
			u8.setFirstname("Erik");
			u8.setLastname("McErik");
			u8.setRole("USER");
			u8.setEnabled(true);
			u8.setPassword("erik");

			UserEntity u9 = new UserEntity();
			u9.setUsername("bjorn");
			u9.setFirstname("Bjorn");
			u9.setLastname("McBjorn");
			u9.setRole("USER");
			u9.setEnabled(true);
			u9.setPassword("bjorn");

			UserEntity u10 = new UserEntity();
			u10.setUsername("pelle");
			u10.setFirstname("Pelle");
			u10.setLastname("McPelle");
			u10.setRole("USER");
			u10.setEnabled(true);
			u10.setPassword("pelle");

			UserEntity u11 = new UserEntity();
			u11.setUsername("tuva");
			u11.setFirstname("Tuva");
			u11.setLastname("McTuva");
			u11.setRole("USER");
			u11.setEnabled(true);
			u11.setPassword("tuva");

			UserEntity u12 = new UserEntity();
			u12.setUsername("meja");
			u12.setFirstname("Meja");
			u12.setLastname("McMeja");
			u12.setRole("USER");
			u12.setEnabled(true);
			u12.setPassword("meja");


			u2.addOrganization(o1);
			u3.addOrganization(o6);
			u4.addOrganization(o2);
			u4.addOrganization(o3);
			u5.addOrganization(o4);
			u6.addOrganization(o4);
			u6.addOrganization(o5);
			u7.addOrganization(o6);
			u8.addOrganization(o6);
			u8.addOrganization(o7);
			u9.addOrganization(o8);
			u10.addOrganization(o3);
			u10.addOrganization(o2);
			u11.addOrganization(o7);
			u11.addOrganization(o8);
			u12.addOrganization(o5);

			userService.addUser(u1);
			userService.addUser(u2);
			userService.addUser(u3);
			userService.addUser(u4);
			userService.addUser(u5);
			userService.addUser(u6);
			userService.addUser(u7);
			userService.addUser(u8);
			userService.addUser(u9);
		};
	}
}

