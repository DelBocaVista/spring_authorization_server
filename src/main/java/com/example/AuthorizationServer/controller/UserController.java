package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * @author Jonas Lundvall (jonlundv@kth.se), Gustav Kavtaradze (guek@kth.se)
 *
 * Controller for REST API requests for user entities with role USER
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    String role = "USER";

    @Autowired
    private UserService userService;

    /**
     * Retrieve all user entities with role USER
     *
     * @return the response entity
     */
    @GetMapping("/")
    public Object getAllUsers() {
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
    @PutMapping("/changePassword/{id}")
    public UserEntityDTO updateUserPassword(@RequestBody UserEntityExtendedDTO userEntityExtendedDTO, @PathVariable Long id) {
        return userService.updatePassword(role, id, userEntityExtendedDTO);
    }

    // Remove this later?!
    /**
     * Change role of a user entity with current role USER
     *
     * @param userEntityDTO the user entity with updated role
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/changeRole/{id}")
    public UserEntityDTO updateUserRole(@RequestBody UserEntityDTO userEntityDTO, @PathVariable Long id) {
        return userService.updateRole(role, id, userEntityDTO);
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