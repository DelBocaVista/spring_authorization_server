package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for user entities with admin role. Only the superadmin role has access to this
 * resource. General access is upheld through http security configuration in ResourceServerConfig. Any endpoint specific
 * access rules are implemented in their respective methods.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/users")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Superadmin is only authorized to handle user entities with admin role
    private final String role = "ADMIN";

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService orgService;

    /**
     * Retrieve all admins.
     *
     * @return the response entity.
     */
    @GetMapping("admins/")
    public ResponseEntity<?> getAllAdmins() {
        List<UserEntityDTO> userEntityDTOS = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        return new ResponseEntity<>(userEntityDTOS, HttpStatus.OK);
    }

    /**
     * Retrieve a single admin by id.
     *
     * @param id the id of the admin.
     * @return the response entity.
     */
    @GetMapping("admins/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        UserEntityDTO userEntityDTO;
        try {
            logger.info("Fetching UserEntity with id {} and role ADMIN", id);
            userEntityDTO = userService.getUserByRoleAndId(role, id);
        } catch (Exception e) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userEntityDTO, HttpStatus.OK);
    }

    /**
     * Create a new admin.
     *
     * @param userEntityDTO the admin user entity dto.
     * @return the response entity.
     */
    @PostMapping("admins/")
    public ResponseEntity<?> addAdminUser(@RequestBody UserEntityExtendedDTO userEntityDTO) {
        // Admin should be member of only one root parent organization
        if(!this.isAdminDTOMemberOfOnlyRootParent(userEntityDTO))
            return new ResponseEntity<>("Unexpected error. Admin can only be member of one root parent " +
                    "organization.", HttpStatus.BAD_REQUEST);

        UserEntityDTO newUserEntityDTO = userService.addUser(role, userEntityDTO);
        return new ResponseEntity<>(newUserEntityDTO, HttpStatus.OK);
    }

    /**
     * Update an existing admin.
     *
     * @param userEntityDTO the new version of the admin user entity.
     * @param id the id of the admin user entity to be updated.
     * @return the response entity.
     */
    @PutMapping("admins/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserEntityExtendedDTO userEntityDTO, @PathVariable Long id) {
        // Admin should be member of only one root parent organization
        if(!this.isAdminDTOMemberOfOnlyRootParent(userEntityDTO))
            return new ResponseEntity<>("Unexpected error. Admin can only be member of one root parent " +
                    "organization.", HttpStatus.BAD_REQUEST);

        UserEntityDTO newUserEntityDTO = userService.updateUser(role, id, userEntityDTO);
        return new ResponseEntity<>(newUserEntityDTO, HttpStatus.OK);
    }

    /**
     * Delete an admin.
     *
     * @param id the id of the user entity to be deleted.
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
     * @param admin the admin user entity dto to be evaluated.
     * @return true if admin is member of only one root parent organization otherwise false.
     */
    private boolean isAdminDTOMemberOfOnlyRootParent(UserEntityDTO admin) {
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
