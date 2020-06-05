package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserDTO;
import com.example.AuthorizationServer.bo.dto.UserExtendedDTO;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for users with admin role. Only the superadmin role has access to this
 * resource. General access is upheld through http security configuration in ResourceServerConfig. Any endpoint specific
 * access rules are implemented in their respective methods.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/users")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Superadmin is only authorized to handle users with admin role
    private final String role = "ADMIN";

    private final UserService userService;

    private final OrganizationService orgService;

    @Autowired
    public AdminController(UserService userService, OrganizationService orgService) {
        this.userService = userService;
        this.orgService = orgService;
    }

    /**
     * Retrieve all admins.
     *
     * @return the response entity.
     */
    @GetMapping("admins/")
    public ResponseEntity<?> getAllAdmins() {
        List<UserDTO> userDTOS = userService.getAllActiveUsersByRole(role);
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    /**
     * Retrieve a single admin by id.
     *
     * @param id the id of the admin.
     * @return the response entity.
     */
    @GetMapping("admins/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        UserDTO userDTO;
        try {
            logger.info("Fetching User with id {} and role ADMIN", id);
            userDTO = userService.getUserByRoleAndId(role, id);
        } catch (Exception e) {
            logger.error("User with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Create a new admin.
     *
     * @param userDTO the admin user dto.
     * @return the response entity.
     */
    @PostMapping("admins/")
    public ResponseEntity<?> addAdminUser(@RequestBody UserExtendedDTO userDTO) {
        // Admin should be member of only one root parent organization
        if(!this.isAdminDTOMemberOfOnlyRootParent(userDTO))
            return new ResponseEntity<>("Unexpected error. Admin can only be member of one root parent " +
                    "organization.", HttpStatus.BAD_REQUEST);

        UserDTO newUserDTO = userService.addUser(role, userDTO);
        return new ResponseEntity<>(newUserDTO, HttpStatus.OK);
    }

    /**
     * Update an existing admin.
     *
     * @param userDTO the new version of the admin user.
     * @param id the id of the admin user to be updated.
     * @return the response entity.
     */
    @PutMapping("admins/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserExtendedDTO userDTO, @PathVariable Long id) {
        // Admin should be member of only one root parent organization
        if(!this.isAdminDTOMemberOfOnlyRootParent(userDTO))
            return new ResponseEntity<>("Unexpected error. Admin can only be member of one root parent " +
                    "organization.", HttpStatus.BAD_REQUEST);

        UserDTO newUserDTO = userService.updateUser(role, id, userDTO);
        return new ResponseEntity<>(newUserDTO, HttpStatus.OK);
    }

    /**
     * Delete an admin.
     *
     * @param id the id of the admin user to be deleted.
     */
    @DeleteMapping("admins/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        try {
            userService.deleteUser(role, id);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error. User with role not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully deleted admin user with id " + id + ".", HttpStatus.OK);
    }

    /**
     * Evaluates if provided admin is member of only one organization which in turn is a root parent organization.
     *
     * @param admin the admin user dto to be evaluated.
     * @return true if admin is member of only one root parent organization otherwise false.
     */
    private boolean isAdminDTOMemberOfOnlyRootParent(UserDTO admin) {
        // Admin should only be member of one organization..
        if(admin.getOrganizations().size() > 1)
            return false;
        // ..and that organization must be a root organization
        for (OrganizationDTO o: admin.getOrganizations()) {
            if(!orgService.isRootOrganization(o.getId()))
                return false;
        }
        return true;
    }
}
