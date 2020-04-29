package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    // @Autowired
    // private OrganizationService orgService;

    /**
     * Retrieve all user entities with role USER
     *
     * @return the response entity
     */
    @GetMapping("/")
    public Object getAllUsers() {
        List<UserEntity> userEntities = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
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
        logger.info("Fetching UserEntity with id {}", id);
        Optional<UserEntity> user = userService.getUserByRoleAndId(role, id);
        if (!user.isPresent()) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserEntityDTO userDto = convertToDto(user.get());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Create a user entity with role USER
     *
     * @param userEntity the user entity to be created
     * @return the response entity
     */
    @PostMapping("/")
    public UserEntityDTO addUser(@RequestBody UserEntity userEntity) {
        UserEntity addedUser = userService.addUser(role, userEntity);
        return convertToDto(addedUser);
    }

    /**
     * Update a user entity with role USER
     *
     * @param userEntity the new version of the user entity
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/{id}")
    public UserEntityDTO updateUser(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        UserEntity updatedUser = userService.updateUser(role, id, userEntity);
        UserEntityDTO userDto = convertToDto(updatedUser);
        return userDto;
    }

    /**
     * Change password for a user entity with role USER
     *
     * @param userEntity the user entity with updated password
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/changePassword/{id}")
    public UserEntityDTO updateUserPassword(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        UserEntity updatedUser = userService.updatePassword(role, id, userEntity);
        UserEntityDTO userDto = convertToDto(updatedUser);
        return userDto;
    }

    // Remove this later?!
    /**
     * Change role of a user entity with current role USER
     *
     * @param userEntityDto the user entity with updated role
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/changeRole/{id}")
    public UserEntityDTO updateUserRole(@RequestBody UserEntityDTO userEntityDto, @PathVariable Long id) {
        UserEntity userEntity = convertToEntity(userEntityDto);
        UserEntity updatedUser = userService.updateRole(role, id, userEntity);
        return convertToDto(updatedUser);
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
     * Convert user entity to user entity dto
     * @param userEntity the user entity to convert
     * @return the corresponding user entity dto
     */
    private UserEntityDTO convertToDto(UserEntity userEntity) {
        UserEntityDTO userEntityDTO = modelMapper.map(userEntity, UserEntityDTO.class);

        // Do something else if needed..?

        return userEntityDTO;
    }

    /**
     * Convert user entity dto to user entity
     * @param userEntityDto the user entity dto to convert
     * @return the corresponding user entity
     */
    private UserEntity convertToEntity(UserEntityDTO userEntityDto) throws ParseException {
        UserEntity newUser = modelMapper.map(userEntityDto, UserEntity.class);
        Optional<UserEntity> user = userService.getUserByRoleAndId(role, userEntityDto.getId());
        return user.orElse(null);
    }
}