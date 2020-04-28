package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.services.OrganizationService;
import com.example.AuthorizationServer.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for UserEntity with role ADMIN
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/organization")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // String role = "ADMIN";

    @Autowired
    private OrganizationService orgService;

    /*
        Get all organizations
     */
    @GetMapping("/")
    public Object getAllOrganizations() {
        List<Organization> organizations = orgService.getAll();
        return new ResponseEntity<>(organizations, HttpStatus.OK);
    }

    /*
        Get single organization by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long id) {
        logger.info("Fetching Organization with id {}", id);
        Organization organization = orgService.getOrganizationById(id);
        if (organization == null) {
            logger.error("Organization with id {} not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // UserDto userDto = convertToDto(user.get());

        return new ResponseEntity<>(organization, HttpStatus.OK); // Change this later (user.get())
    }

    @PostMapping("/")
    public Organization addOrganization(@RequestBody Organization organization) {
        return orgService.addOrganization(organization);
    }

    @PostMapping("/{parentId}")
    public Organization addOrganization(@RequestBody Organization organization, @PathVariable Long parentId) {
        Organization parent = orgService.getOrganizationById(parentId);
        return orgService.addParentToOrganization(organization, parent);
    }

    @PutMapping("/{id}")
    public Organization updateOrganization(@RequestBody Organization organization, @PathVariable Long id) {
        return orgService.updateOrganization(id, organization);
    }

    @PutMapping("/changeParent/{childId}/{parentId}")
    public Organization updateOrganizationParent(@PathVariable Long childId, @PathVariable Long parentId) {
        Organization child = orgService.getOrganizationById(childId);
        Organization parent = orgService.getOrganizationById(parentId);
        return orgService.addParentToOrganization(child, parent);
    }

    @GetMapping("/children/all/{id}")
    public List<Organization> getAllChildrenOfOrganization(@PathVariable Long id) {
        Organization organization = orgService.getOrganizationById(id);
        return orgService.getAllChildrenOfOrganization(organization);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        orgService.deleteOrganization(id);
    }
}
