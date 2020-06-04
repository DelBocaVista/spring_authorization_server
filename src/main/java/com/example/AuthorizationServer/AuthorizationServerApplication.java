package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.entity.User;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import com.example.AuthorizationServer.repository.UserRepository;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

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
			User sudo = new User();
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
	private UserRepository userRep;
	@Autowired
	private OrganizationRepository orgRep;

	@Bean
	InitializingBean seedDatabase() {
		return () -> {

			// ORGANIZATIONS
			List<Organization> organizationList = new ArrayList<>();

			// Define your organizations
			String[] organizationNames = {"KTH", "STH", "SCI", "Teachers", "Students", "SU", "Engelska Institutionen",
					"Historiska institutionen"};

			for (String organizationName : organizationNames) {
				Organization o = new Organization();
				o.setName(organizationName);
				o.setEnabled(true);
				organizationList.add(o);
			}

			Iterable<Organization> organizationsInDb = orgRep.saveAll(organizationList);
			HashMap<String, Organization> organizationHashMap = new HashMap<>();

			for (Organization o : organizationsInDb) {
				Organization orgInDb = orgRep.save(o);
				organizationHashMap.put(orgInDb.getName(), orgInDb);
			}

			// Define a parent for each created organization (root organizations are set as parent of itself)
			String[] parents = {"KTH", "KTH", "KTH", "STH", "STH", "SU", "SU", "SU"};

			for (int i = 0; i < organizationNames.length; i++) {
				Organization c = organizationHashMap.get(organizationNames[i]);
				Organization p = organizationHashMap.get(parents[i]);
				c.setParent(p);
				organizationHashMap.put(organizationNames[i], orgRep.save(c));
			}

			// USERS
			List<User> users = new ArrayList<>();

			User u1 = new User();
			u1.setUsername("sudo");
			u1.setFirstname("Sudo");
			u1.setLastname("McSudo");
			u1.setRole("SUPERADMIN");
			u1.setEnabled(true);
			u1.setPassword(new BCryptPasswordEncoder().encode("sudo"));
			users.add(u1);

			User u2 = new User();
			u2.setUsername("kthadmin");
			u2.setFirstname("Admin");
			u2.setLastname("McAdmin");
			u2.setRole("ADMIN");
			u2.setEnabled(true);
			u2.setPassword(new BCryptPasswordEncoder().encode("kthadmin"));
			u2.addOrganization(organizationHashMap.get("KTH"));
			users.add(u2);

			User u3 = new User();
			u3.setUsername("suadmin");
			u3.setFirstname("Admin");
			u3.setLastname("McAdmin");
			u3.setRole("ADMIN");
			u3.setEnabled(true);
			u3.setPassword(new BCryptPasswordEncoder().encode("suadmin"));
			u3.addOrganization(organizationHashMap.get("SU"));
			users.add(u3);

			User u4 = new User();
			u4.setUsername("kthuser");
			u4.setFirstname("User");
			u4.setLastname("McUser");
			u4.setRole("USER");
			u4.setEnabled(true);
			u4.setPassword(new BCryptPasswordEncoder().encode("kthuser"));
			u4.addOrganization(organizationHashMap.get("STH"));
			u4.addOrganization(organizationHashMap.get("SCI"));
			users.add(u4);

			User u5 = new User();
			u5.setUsername("jonas");
			u5.setFirstname("Jonas");
			u5.setLastname("McJonas");
			u5.setRole("USER");
			u5.setEnabled(true);
			u5.setPassword(new BCryptPasswordEncoder().encode("jonas"));
			u5.addOrganization(organizationHashMap.get("Teachers"));
			users.add(u5);

			User u6 = new User();
			u6.setUsername("gustav");
			u6.setFirstname("Gustav");
			u6.setLastname("McGustav");
			u6.setRole("USER");
			u6.setEnabled(true);
			u6.setPassword(new BCryptPasswordEncoder().encode("gustav"));
			u6.addOrganization(organizationHashMap.get("Teachers"));
			u6.addOrganization(organizationHashMap.get("Students"));
			users.add(u6);

			User u7 = new User();
			u7.setUsername("andreas");
			u7.setFirstname("Andreas");
			u7.setLastname("McAndreas");
			u7.setRole("USER");
			u7.setEnabled(true);
			u7.setPassword(new BCryptPasswordEncoder().encode("andreas"));
			u7.addOrganization(organizationHashMap.get("SU"));
			users.add(u7);

			User u8 = new User();
			u8.setUsername("erik");
			u8.setFirstname("Erik");
			u8.setLastname("McErik");
			u8.setRole("USER");
			u8.setEnabled(true);
			u8.setPassword(new BCryptPasswordEncoder().encode("erik"));
			u8.addOrganization(organizationHashMap.get("SU"));
			u8.addOrganization(organizationHashMap.get("Engelska Institutionen"));
			users.add(u8);

			User u9 = new User();
			u9.setUsername("bjorn");
			u9.setFirstname("Bjorn");
			u9.setLastname("McBjorn");
			u9.setRole("USER");
			u9.setEnabled(true);
			u9.setPassword(new BCryptPasswordEncoder().encode("bjorn"));
			u9.addOrganization(organizationHashMap.get("Historiska institutionen"));
			users.add(u9);

			User u10 = new User();
			u10.setUsername("pelle");
			u10.setFirstname("Pelle");
			u10.setLastname("McPelle");
			u10.setRole("USER");
			u10.setEnabled(true);
			u10.setPassword(new BCryptPasswordEncoder().encode("pelle"));
			u10.addOrganization(organizationHashMap.get("SCI"));
			u10.addOrganization(organizationHashMap.get("STH"));
			users.add(u10);

			User u11 = new User();
			u11.setUsername("tuva");
			u11.setFirstname("Tuva");
			u11.setLastname("McTuva");
			u11.setRole("USER");
			u11.setEnabled(true);
			u11.setPassword(new BCryptPasswordEncoder().encode("tuva"));
			u11.addOrganization(organizationHashMap.get("Engelska Institutionen"));
			u11.addOrganization(organizationHashMap.get("Historiska institutionen"));
			users.add(u11);

			User u12 = new User();
			u12.setUsername("meja");
			u12.setFirstname("Meja");
			u12.setLastname("McMeja");
			u12.setRole("USER");
			u12.setEnabled(true);
			u12.setPassword(new BCryptPasswordEncoder().encode("meja"));
			u12.addOrganization(organizationHashMap.get("Students"));
			users.add(u12);

			userRep.saveAll(users);
		};
	}
}

