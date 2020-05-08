package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.service.UserService;
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
			o4.setName("4th Floor");
			o4.setEnabled(true);

			Organization o5 = new Organization();
			o5.setName("5th Floor");
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

			userService.addUser(u1);
			userService.addUser(u2);
			userService.addUser(u3);
			userService.addUser(u4);
			userService.addUser(u5);
			userService.addUser(u6);
			userService.addUser(u7);
			userService.addUser(u8);
			userService.addUser(u9);
			//orgService.addOrganization(o1);


			/*UserEntity u1 = new UserEntity();
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
			u3.setUsername("kthuser");
			u3.setFirstname("User");
			u3.setLastname("McUser");
			u3.setRole("USER");
			u3.setEnabled(true);
			u3.setPassword("kthuser");

			Organization o1 = new Organization();
			o1.setName("KTH");
			o1.setEnabled(true);
			o1.addUser(u1);

			// u1.addOrganization(o1);
			u2.addOrganization(o1);
			u3.addOrganization(o1);

			orgService.addOrganizationSeed(o1);
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
			o5.setName("4th Floor");
			o5.setEnabled(true);

			Organization o6 = new Organization();
			o5.setName("5th Floor");
			o5.setEnabled(true);

			Organization o7 = new Organization();
			o5.setName("SU");
			o5.setEnabled(true);

			//Organization o1new = o.findByName("KTH");

			orgService.addOrganizationSeed(o2);
			orgService.addOrganizationSeed(o3);
			orgService.addOrganizationSeed(o4);
			orgService.addOrganizationSeed(o5);
			orgService.addOrganizationSeed(o7);

			orgService.addParentToOrganization(o2,o1);
			orgService.addParentToOrganization(o3,o1);
			orgService.addParentToOrganization(o4,o1);
			orgService.addParentToOrganization(o5,o2);
			orgService.addParentToOrganization(o6,o2);

			Organization or = orgService.getOrganizationByIdSeed(3L);
			System.out.println("3L " + or.getName());
			UserEntity u = userService.getUserByUsername("user");
			System.out.println(u);
			u.addOrganization(or);
			System.out.println("u " + u.getUsername());
			for (Organization o: u.getOrganizations()) {
				System.out.println(o.getName());
			}

			System.out.println(userService.updateUser("USER", u.getId(), u));

			System.out.println("Children");
			List<OrganizationDTO> res = orgService.getAllChildrenOfOrganization(o1.getId());
			for (OrganizationDTO o:res) {
				System.out.println(o.getName());
			}

            Organization o8 = new Organization();
            o8.setName("Dolor");
            o8.setEnabled(true);

            Organization o9 = new Organization();
            o9.setName("Frans");
            o9.setEnabled(true);

            orgService.addOrganizationSeed(o6);
            orgService.addOrganizationSeed(o7);
            orgService.addOrganizationSeed(o8);
            orgService.addOrganizationSeed(o9);

            orgService.addParentToOrganization(o7,o6);
            orgService.addParentToOrganization(o8,o6);
            orgService.addParentToOrganization(o9,o7);

            System.out.println("Get All");
			res = orgService.getAllOrganizations();

            System.out.println(orgService.prettyPrint(res));

            System.out.println("Get All Children of 1"); // NEED TO ADD SORTING?
            OrganizationDTO org = orgService.getOrganizationDTOById(1L);
            res = orgService.getAllChildrenOfOrganization(org.getId());

            System.out.println(orgService.prettyPrint(res));

            System.out.println("Get Direct Children of 1"); // NEED TO ADD SORTING?
            res = orgService.getDirectChildrenOfOrganization(org);

            System.out.println(orgService.prettyPrint(res));

			System.out.println("Change parent");
			orgService.changeParentOfOrganization(7L,5L);
			res = orgService.getAllOrganizations();

			System.out.println(orgService.prettyPrint(res));*/
		};
	}
}
