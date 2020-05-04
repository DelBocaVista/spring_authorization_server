package com.example.AuthorizationServer;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.services.OrganizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthorizationServerApplicationTests {

	@Autowired
	private OrganizationService orgService;

	@Test
	void contextLoads() {
	}

	@Test
	void testGetOrganizationChildren() {

	    /*Organization o1 = new Organization();
		o1.setName("KTH");
		o1.setEnabled(true);

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

		orgService.addOrganizationSeed(o1);
		orgService.addOrganizationSeed(o2);
		orgService.addOrganizationSeed(o3);
		orgService.addOrganizationSeed(o4);
		orgService.addOrganizationSeed(o5);

		orgService.addParentToOrganization(o2,o1);
		orgService.addParentToOrganization(o3,o1);
		orgService.addParentToOrganization(o4,o1);
		orgService.addParentToOrganization(o5,o2);

		Organization o2InDB = orgService.getOrganizationByName(o2.getName());
		Organization o3InDB = orgService.getOrganizationByName(o3.getName());
		Organization o4InDB = orgService.getOrganizationByName(o4.getName());
		List<Organization> expected = new ArrayList<>();
		expected.add(o2InDB);
		expected.add(o3InDB);
		expected.add(o4InDB);

		List<Organization> allChildrenDB = orgService.getAllChildrenOfOrganization(o1);

		assertEquals(expected, allChildrenDB);
		*/
	}

}
