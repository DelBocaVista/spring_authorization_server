package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.service.UserService;
import org.modelmapper.ModelMapper;
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
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for UserEntity with role ADMIN
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/users")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    String role = "ADMIN";

    @Autowired
    private UserService userService;

    /**
     * Retrieve all admins
     * @return
     */
    @GetMapping("admins/")
    public ResponseEntity<?> getAllAdmins() {
        List<UserEntityDTO> userEntityDTOS = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        return new ResponseEntity<>(userEntityDTOS, HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("admins/{id}")
    public ResponseEntity<UserEntityDTO> getAdminById(@PathVariable Long id) {
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
     *
     * @param userEntity
     * @return
     */
    @PostMapping("admins/")
    public UserEntityDTO addUser(@RequestBody UserEntityExtendedDTO userEntity) {
        return userService.addUser(role, userEntity);
    }

    /**
     *
     * @param userEntity
     * @param id
     * @return
     */
    @PutMapping("admins/{id}")
    public UserEntity updateUser(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updateUser(role, id, userEntity);
    }

    /**
     *
     * @param userEntityDTO
     * @param id
     * @return
     */
    @PutMapping("admins/changePassword/{id}")
    public UserEntityDTO updateUserPassword(@RequestBody UserEntityExtendedDTO userEntityDTO, @PathVariable Long id) {
        return userService.updatePassword(role, id, userEntityDTO);
    }

    /**
     *
     * @param userEntityDTO
     * @param id
     * @return
     */
    @PutMapping("admins/changeRole/{id}")
    public UserEntityDTO updateUserRole(@RequestBody UserEntityDTO userEntityDTO, @PathVariable Long id) {
        return userService.updateRole(role, id, userEntityDTO);
    }

    /**
     *
     * @param id
     */
    @DeleteMapping("admins/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(role, id);
    }
}
