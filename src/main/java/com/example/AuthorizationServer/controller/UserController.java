package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.security.CustomUserDetails;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.service.UserService;
import com.example.AuthorizationServer.utility.UserDetailExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.util.*;

/**
 * @author Jonas Lundvall (jonlundv@kth.se), Gustav Kavtaradze (guek@kth.se)
 *
 * Controller for REST API requests for user entities with user role. Only the admin role has access to this
 * resource. General access is upheld through http security configuration in ResourceServerConfig. Any endpoint specific
 * access rules are implemented in their respective methods.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Admin is only authorized to handle user entities with admin role
    private final String role = "USER";

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService orgService;

    /**
     * Retrieve all user entities with role user.
     *
     * @return the response entity.
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        // Admin should only belong to one organization
        if(user.getOrganizations().size() != 1)
            return new ResponseEntity<>("Unexpected error. Only one organization membership expected.", HttpStatus.BAD_REQUEST);

        List<UserEntityDTO> userEntities = new ArrayList<>();
        // Admin is only authorized to fetch users from its own organization
        for (OrganizationDTO o: user.getOrganizations()) {
            try {
                userEntities = userService.getAllUsersByOrganization(o.getId());
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>("Unexpected error. Organization not found.", HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    /**
     * Retrieve a single user entity with user role by id.
     *
     * @param id the user entity id.
     * @return the response entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        // Admin should only belong to one organization
        if(user.getOrganizations().size() != 1)
            return new ResponseEntity<>("Unexpected error. Only one organization membership expected.", HttpStatus.BAD_REQUEST);

        UserEntityDTO userEntityDTO;
        try {
            logger.info("Fetching UserEntity with id {}", id);
            userEntityDTO = userService.getUserByRoleAndId(role, id);
        } catch (EntityNotFoundException e) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Admin is only authorized to fetch users from its own organization
        if(!areMembersOfTheSameOrganization(user.getOrganizations(), userEntityDTO.getOrganizations()))
            return new ResponseEntity<>("Unexpected error. Not authorized to fetch user.", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(userEntityDTO, HttpStatus.OK);
    }

    /**
     * Create a user entity with user role.
     *
     * @param userEntityDTO the user entity dto.
     * @return the response entity.
     */
    @PostMapping("/")
    public ResponseEntity<?> addUser(@RequestBody UserEntityExtendedDTO userEntityDTO) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        // Admin should only belong to one organization
        if(user.getOrganizations().size() != 1)
            return new ResponseEntity<>("Unexpected error. Only one organization membership expected.", HttpStatus.BAD_REQUEST);

        for (OrganizationDTO oUser: user.getOrganizations()) {
            for (OrganizationDTO oDTO: userEntityDTO.getOrganizations()) {
                if(!orgService.isOrganizationChildOfRootParent(oDTO.getId(), oUser.getId()))
                    return new ResponseEntity<>("Unexpected error. Not authorized to add user to organization.", HttpStatus.UNAUTHORIZED);
            }
        }

        UserEntityDTO addedUserEntityDTO;

        try {
            addedUserEntityDTO = userService.addUser(role, userEntityDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. User is not valid.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(addedUserEntityDTO, HttpStatus.OK);
    }

    /**
     * Update a user entity with role USER
     *
     * @param userEntityExtendedDTO the new version of the user entity
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/{id}")
    public UserEntityDTO updateUser(@RequestBody UserEntityExtendedDTO userEntityExtendedDTO, @PathVariable Long id) {
        return userService.updateUser(role, id, userEntityExtendedDTO);
    }

    /**
     * Change password for a user entity with role USER
     *
     * @param userEntityExtendedDTO the user entity with updated password
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/{id}/changePassword/")
    public UserEntityDTO updateUserPassword(@RequestBody UserEntityExtendedDTO userEntityExtendedDTO, @PathVariable Long id) {
        return userService.updatePassword(role, id, userEntityExtendedDTO);
    }

    // Remove this later?!
    /**
     * Delete a user entity with role USER
     *
     * @param id the id of the user entity to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(role, id);
    }

    /**
     * Verify that the used access token has authority to reach the /user request mapping
     */
    @GetMapping("/verify")
    public @ResponseBody boolean verifyToken(){
        return true;
    }

    /**
     * Create multiple users by JSON array.
     *
     * @param userEntityExtendedDTOS the JSON array.
     * @return the response entity.
     */
    @PostMapping("/upload/json/")
    public ResponseEntity<?> createUsersFromJSONArray(@RequestBody List<UserEntityExtendedDTO> userEntityExtendedDTOS) {

        for (UserEntityExtendedDTO u: userEntityExtendedDTOS) {
            if (u.getRole().equals("USERS"))
                return new ResponseEntity<>("Not authorized to create other roles than USER.", HttpStatus.UNAUTHORIZED);
        }
        userService.addUsers(userEntityExtendedDTOS);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Create multiple users by csv file.
     *
     * @param file the csv file.
     * @return the response entity.
     */
    @PostMapping("/upload/file/")
    public ResponseEntity<?> createUsersFromCSVFile(@RequestParam("file") MultipartFile file) {

        if(file.isEmpty())
            return new ResponseEntity<>("Unexpected error. File is empty.", HttpStatus.NO_CONTENT);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            String split = ",";
            List<UserEntityExtendedDTO> userEntityDTOS = new ArrayList<>();

            line = br.readLine();
            String[] headersInFile = line.split(split);
            String[] expectedHeaders = {"firstname", "lastname", "username", "password", "enabled", "organizations"};

            if(headersInFile.length != expectedHeaders.length)
                return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);

            for (int i = 0; i < expectedHeaders.length; i++) {
                if(!headersInFile[i].equals(expectedHeaders[i]))
                    return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);
            }

            HashMap<String, OrganizationDTO> fetchedOrganizations = new HashMap<>();

            while ((line = br.readLine()) != null) {
                String[] userArray = line.split(split);

                int i = 0;

                UserEntityExtendedDTO user = new UserEntityExtendedDTO();
                user.setFirstname(userArray[i++]);
                user.setLastname(userArray[i++]);
                user.setUsername(userArray[i++]);
                user.setPassword(userArray[i++]);
                user.setRole("USER");
                user.setEnabled(new Boolean(userArray[i++]));

                // All the following columns are expected to be names of existing organizations.
                Set<OrganizationDTO> organizations = new HashSet<>();

                while (i < userArray.length) {

                    if(fetchedOrganizations.containsKey(userArray[i])) {
                        organizations.add(fetchedOrganizations.get(userArray[i]));
                    } else {
                        OrganizationDTO organizationDTO = orgService.getOrganizationByName(userArray[i]);

                        if(organizationDTO == null)
                            return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);

                        for (OrganizationDTO o: user.getOrganizations()) {
                            // Admin is only allowed to add users to organizations within its own organization tree
                            if(orgService.isOrganizationChildOfRootParent(organizationDTO.getId(), o.getId()))
                                return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);

                        }

                        fetchedOrganizations.put(userArray[i], organizationDTO);
                        organizations.add(organizationDTO);
                    }

                    i++;
                }

                user.setOrganizations(organizations);

                userEntityDTOS.add(user);
            }

            userService.addUsers(userEntityDTOS);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. Could not read file.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean areMembersOfTheSameOrganization(Collection<OrganizationDTO> first, Collection<OrganizationDTO> second) {
        for (OrganizationDTO oFirst: first) {
            for (OrganizationDTO oSecond: second) {
                if(oFirst.getId().equals(oSecond.getId()))
                    return true;
            }
        }
        return false;
    }
}