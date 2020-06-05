package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserDTO;
import com.example.AuthorizationServer.bo.dto.UserExtendedDTO;
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
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se), Gustav Kavtaradze (guek@kth.se), Erik Wikzén (wikzen@kth.se)
 *
 * Controller for REST API requests for users with user role. Only the admin role has access to this resource. General
 * access is upheld through http security configuration in ResourceServerConfig. Any endpoint specific access rules are
 * implemented in their respective methods.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Admin is only authorized to handle users with admin role
    private final String role = "USER";

    private final UserService userService;

    private final OrganizationService orgService;

    @Autowired
    public UserController(UserService userService, OrganizationService orgService) {
        this.userService = userService;
        this.orgService = orgService;
    }

    /**
     * Retrieve all users with role user.
     *
     * @param limit the upper limit of how many users to retrieve.
     * @param offset the offset for where in the ordered listing to start retrieving users.
     * @return the response entity.
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer offset) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        List<UserDTO> users;

        int getLimit = 20;
        int getOffset = 0;

        if (limit != null)
            getLimit = limit;

        if (offset != null)
            getOffset = offset;

        // Admin is only authorized to fetch users with membership in sub organizations of its own root organization
        try {
            users = userService.getAllUsersByRootOrganization(adminOrganization.getId(), getLimit, getOffset);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. Organization not found.", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieve a single users with user role by id.
     *
     * @param id the users id.
     * @return the response entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO;
        try {
            logger.info("Fetching User with id {}", id);
            userDTO = userService.getUserByRoleAndId(role, id);
        } catch (EntityNotFoundException e) {
            logger.error("User with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Admin can only fetch user with membership in sub organizations of its own root organization
        if(!isUserMemberOfSubOrganizationOfRootOrganization(userDTO, adminOrganization))
            return new ResponseEntity<>("Unexpected error. Not authorized to fetch user.", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Create a user with user role.
     *
     * @param userDTO the user dto.
     * @return the response entity.
     */
    @PostMapping("/")
    public ResponseEntity<?> addUser(@RequestBody UserExtendedDTO userDTO) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        // Admin can only create user with membership in sub organizations of its own root organization
        if(!isUserMemberOfSubOrganizationOfRootOrganization(userDTO, adminOrganization))
            return new ResponseEntity<>("Unexpected error. Not authorized to create user.", HttpStatus.UNAUTHORIZED);

        UserDTO createdUserDTO;

        try {
            createdUserDTO = userService.addUser(role, userDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. User is not valid.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createdUserDTO, HttpStatus.OK);
    }

    /**
     * Update a user with user role.
     *
     * @param userDTO the new version of the user dto.
     * @param id the id of the user to be updated.
     * @return the response entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserExtendedDTO userDTO, @PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        // Admin can only update user with membership in sub organizations of its own root organization
        if(!isUserMemberOfSubOrganizationOfRootOrganization(userDTO, adminOrganization))
            return new ResponseEntity<>("Unexpected error. Not authorized to update user.", HttpStatus.UNAUTHORIZED);

        UserDTO updatedUserDTO = userService.updateUser(role, id, userDTO);

        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

    /**
     * Delete a user with user role.
     *
     * @param id the id of the user to be deleted.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        UserDTO userToBeDeleted = userService.getUserByRoleAndId("USER", id);

        // Admin can only delete user with membership in sub organization of its own root organization
        if(!isUserMemberOfSubOrganizationOfRootOrganization(userToBeDeleted, adminOrganization))
            return new ResponseEntity<>("Unexpected error. Not authorized to update user.", HttpStatus.UNAUTHORIZED);

        try {
            userService.deleteUser(role, id);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error. User with role not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully deleted user with id " + id + ".", HttpStatus.OK);
    }

    /**
     * Create multiple users by JSON array.
     *
     * @param userDTOS the JSON array.
     * @return the response entity.
     */
    @PostMapping("/upload/json/")
    public ResponseEntity<?> createUsersFromJSONArray(@RequestBody List<UserExtendedDTO> userDTOS) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        for (UserExtendedDTO u: userDTOS) {
            if (u.getRole().equals("USERS"))
                return new ResponseEntity<>("Unexpected error. Not authorized to create other roles than user.", HttpStatus.UNAUTHORIZED);
            if(isUserMemberOfSubOrganizationOfRootOrganization(u, adminOrganization))
                return new ResponseEntity<>("Unexpected error. Not authorized to create user in organization.", HttpStatus.UNAUTHORIZED);
        }

        userService.addUsers(userDTOS);
        return new ResponseEntity<>("Successfully created users.", HttpStatus.OK);
    }


    /**
     * Create multiple users by csv file.
     *
     * @param file the csv file.
     * @return the response entity.
     */
    @PostMapping("/upload/file/")
    public ResponseEntity<?> createUsersFromCSVFile(@RequestParam("file") MultipartFile file) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO adminOrganization;
        try {
            adminOrganization = extractAdminRootOrganization(user.getOrganizations());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Unexpected error. Admin organization membership is invalid.", HttpStatus.BAD_REQUEST);
        }

        if(file.isEmpty())
            return new ResponseEntity<>("Unexpected error. File is empty.", HttpStatus.NO_CONTENT);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            String split = ",";
            List<UserExtendedDTO> userDTOS = new ArrayList<>();

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

                UserExtendedDTO userDTO = new UserExtendedDTO();
                userDTO.setFirstname(userArray[i++]);
                userDTO.setLastname(userArray[i++]);
                userDTO.setUsername(userArray[i++]);
                userDTO.setPassword(userArray[i++]);
                userDTO.setRole("USER");
                userDTO.setEnabled(new Boolean(userArray[i++]));

                // All the following columns are expected to be names of existing organizations.
                Set<OrganizationDTO> organizations = new HashSet<>();

                while (i < userArray.length) {

                    if(fetchedOrganizations.containsKey(userArray[i])) {
                        organizations.add(fetchedOrganizations.get(userArray[i]));
                    } else {
                        OrganizationDTO organizationDTO = orgService.getOrganizationByName(userArray[i]);

                        if(organizationDTO == null)
                            return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);

                        // Admin is only allowed to add users to organizations within its own organization tree
                        if(!orgService.isOrganizationChildOfRootParent(organizationDTO.getId(), adminOrganization.getId()))
                            return new ResponseEntity<>("Unexpected error. Not authorized to create user in organization.", HttpStatus.UNAUTHORIZED);


                        fetchedOrganizations.put(userArray[i], organizationDTO);
                        organizations.add(organizationDTO);
                    }

                    i++;
                }

                userDTO.setOrganizations(organizations);

                userDTOS.add(userDTO);
            }

            userService.addUsers(userDTOS);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. Could not read file.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Helper method that evaluates whether a user is only member of sub organizations to a given root organization.
     *
     * @param user the user.
     * @param root the root organization.
     * @return true if user is only member of sub organizations otherwise false.
     */
    private boolean isUserMemberOfSubOrganizationOfRootOrganization(UserDTO user, OrganizationDTO root) {
        for (OrganizationDTO o: user.getOrganizations()) {
            if(!orgService.isOrganizationChildOfRootParent(o.getId(), root.getId()))
                return false;
        }
        return true;
    }

    /**
     * Helper method for extracting the root organization membership of an admin. User entity membership of
     * organizations are given as a collection. This method that admin is member of only one organization and that the
     * organization is a root organization.
     *
     * @param adminsOrganizations the collection of organizations.
     * @return the root organization that admin is a member of.
     */
    private OrganizationDTO extractAdminRootOrganization(Collection<OrganizationDTO> adminsOrganizations) {
        List<OrganizationDTO> collectionAsList = new ArrayList<>(adminsOrganizations);

        // Admin should only be member of one organization
        if(collectionAsList.size() != 1)
            throw new IllegalArgumentException("Admin is member of more than one organization.");

        // That organization should be a root organization
        if(!orgService.isRootOrganization(collectionAsList.get(0).getId()))
            throw new IllegalArgumentException("Admin is member of a none root organization.");

        return collectionAsList.get(0);
    }
}
