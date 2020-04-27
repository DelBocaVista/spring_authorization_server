package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.entity.UserEntity;
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
@RequestMapping(path="/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    String role = "ADMIN";

    @Autowired
    private UserService userService;

    // @Autowired
    // private OrganizationService orgService;

    /*
        Get all admins
     */
    @GetMapping("/")
    public Object getAllAdmins() {
        List<UserEntity> userEntities = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        if (userEntities == null || userEntities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    // ADD MORE FUNTIONALITY HERE!!
    /*
        Get single student by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getAdminById(@PathVariable Long id) {
        logger.info("Fetching UserEntity with id {} and role ADMIN", id);
        Optional<UserEntity> user = userService.getUserByRoleAndId(role, id);
        if (!user.isPresent()) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // UserDto userDto = convertToDto(user.get());

        return new ResponseEntity<>(user.get(), HttpStatus.OK); // Change this later (user.get())
    }

    @PostMapping("/")
    public UserEntity addUser(@RequestBody UserEntity userEntity) {
        return userService.addUser(role, userEntity);
    }

    @PutMapping("/{id}")
    public UserEntity updateUser(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updateUser(role, id, userEntity);
    }

    @PutMapping("/changePassword/{id}")
    public UserEntity updateUserPassword(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updatePassword(role, id, userEntity);
    }

    @PutMapping("/changeRole/{id}")
    public UserEntity updateUserRole(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updateRole(role, id, userEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(role, id);
    }
}
