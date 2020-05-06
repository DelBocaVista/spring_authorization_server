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

		u1.addOrganization(o1);
		u2.addOrganization(o1);
		u3.addOrganization(o1);

		return () -> {
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
			o5.setName("Doktorander");
			o5.setEnabled(true);

			//Organization o1new = o.findByName("KTH");

			orgService.addOrganizationSeed(o2);
			orgService.addOrganizationSeed(o3);
			orgService.addOrganizationSeed(o4);
			orgService.addOrganizationSeed(o5);

			orgService.addParentToOrganization(o2,o1);
			orgService.addParentToOrganization(o3,o1);
			orgService.addParentToOrganization(o4,o1);
			orgService.addParentToOrganization(o5,o2);

			Organization or = orgService.getOrganizationByIdSeed(3L);
			System.out.println("3L " + or.getName());
			UserEntity u = userService.getUserByUsername("admin");
			System.out.println(u);
			u.addOrganization(or);
			System.out.println("u " + u.getUsername());
			for (Organization o: u.getOrganizations()) {
				System.out.println(o.getName());
			}

			System.out.println(userService.updateUser("ADMIN", u.getId(), u));

			System.out.println("Children");
			List<OrganizationDTO> res = orgService.getAllChildrenOfOrganization(o1.getId());
			for (OrganizationDTO o:res) {
				System.out.println(o.getName());
			}

            Organization o6 = new Organization();
            o6.setName("Lorem");
            o6.setEnabled(true);

            Organization o7 = new Organization();
            o7.setName("Ipsum");
            o7.setEnabled(true);

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

			System.out.println(orgService.prettyPrint(res));
			/*
			var input = ["Fred-Jim-Bob", "Fred-Jim", "Fred-Thomas-Rob", "Fred"];
            var output = [];
            for (var i = 0; i < input.length; i++) {
                var chain = input[i].split("-");
                var currentNode = output;
                for (var j = 0; j < chain.length; j++) {
                    var wantedNode = chain[j];
                    var lastNode = currentNode;
                    for (var k = 0; k < currentNode.length; k++) {
                        if (currentNode[k].name == wantedNode) {
                            currentNode = currentNode[k].children;
                            break;
                        }
                    }
                    // If we couldn't find an item in this list of children
                    // that has the right name, create one:
                    if (lastNode == currentNode) {
                        var newNode = currentNode[k] = {name: wantedNode, children: []};
                        currentNode = newNode.children;
                    }
                }
            }
			 */
		};
	}
}
