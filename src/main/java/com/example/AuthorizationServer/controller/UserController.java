package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.security.CustomUserDetails;
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
 * Controller for REST API requests for user entities with role USER
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    String role = "USER";

    @Autowired
    private UserService userService;

    /**
     * Create multiple users by JSON array
     *
     * @param userEntityExtendedDTOS the JSON array
     * @return the response entity
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
     * Create multiple users by csv file
     *
     * @param file the csv file
     * @return the response entity
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
            String[] expectedHeaders = {"firstname", "lastname", "username", "password", "enabled"};

            if(headersInFile.length != expectedHeaders.length)
                return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);

            for (int i = 0; i < expectedHeaders.length; i++) {
                System.out.println(headersInFile[i] + " " + expectedHeaders[i]);
                if(!headersInFile[i].equals(expectedHeaders[i]))
                    return new ResponseEntity<>("Unexpected error during parsing. CSV file has wrong format.", HttpStatus.BAD_REQUEST);
            }

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

                // HOW TO HANDLE ORGANIZATIONS?
                /*while (i < userArray.length) {
                    // Handle adding organizations?
                }*/

                userEntityDTOS.add(user);
            }

            userService.addUsers(userEntityDTOS);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Unexpected error. Could not read file.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieve all user entities with role USER
     *
     * @return the response entity
     */
    @GetMapping("/")
    public Object getAllUsers() {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        List<UserEntityDTO> userEntities = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        if (userEntities == null || userEntities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    /**
     * Get a single user entity with role USER by id
     *
     * @param id the user entity id
     * @return the response entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntityDTO> getUserById(@PathVariable Long id) {
        UserEntityDTO userEntityDTO;
        try {
            logger.info("Fetching UserEntity with id {}", id);
            userEntityDTO = userService.getUserByRoleAndId(role, id);
        } catch (EntityNotFoundException e) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userEntityDTO, HttpStatus.OK);
    }

    /**
     * Create a user entity with role USER
     *
     * @param userEntityDTO the user entity to be created
     * @return the response entity
     */
    @PostMapping("/")
    public UserEntityDTO addUser(@RequestBody UserEntityExtendedDTO userEntityDTO) {
        return userService.addUser(role, userEntityDTO);
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


}