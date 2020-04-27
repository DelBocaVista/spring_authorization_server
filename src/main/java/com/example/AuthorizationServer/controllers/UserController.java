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
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        logger.info("Fetching UserEntity with id {}", id);
        Optional<UserEntity> user = userService.getUserByRoleAndId(role, id);
        if (!user.isPresent()) {
            logger.error("UserEntity with id {} and role {} not found.", id, role);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // UserDto userDto = convertToDto(user.get());

        return new ResponseEntity<>(user.get(), HttpStatus.OK); // Change this later (user.get())
    }

    /**
     * Create a user entity with role USER
     *
     * @param userEntity the user entity to be created
     * @return the response entity
     */
    @PostMapping("/")
    public UserEntity addUser(@RequestBody UserEntity userEntity) {
        return userService.addUser(role, userEntity);
    }

    /**
     * Update a user entity with role USER
     *
     * @param userEntity the new version of the user entity
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/{id}")
    public UserEntity updateUser(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updateUser(role, id, userEntity);
    }

    /**
     * Change password for a user entity with role USER
     *
     * @param userEntity the user entity with updated password
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/changePassword/{id}")
    public UserEntity updateUserPassword(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updatePassword(role, id, userEntity);
    }

    // Remove this later?!
    /**
     * Change role of a user entity with current role USER
     *
     * @param userEntity the user entity with updated role
     * @param id the id of the user entity to be updated
     * @return the response entity
     */
    @PutMapping("/changeRole/{id}")
    public UserEntity updateUserRole(@RequestBody UserEntity userEntity, @PathVariable Long id) {
        return userService.updateRole(role, id, userEntity);
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
    /*@GetMapping(path="/org/{id}")
    @ResponseBody
    public ResponseEntity<?> findAllByOrgId(@PathVariable Long id) {
        Organization org = new Organization();
        org.setId(Long.valueOf(id));
        Collection<UserEntity> users = (Collection<UserEntity>) userRepository.findByOrganization(org);

        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        /* ArrayList<UserDto> usersDto = new ArrayList<>();

        for (UserEntity u : users) {
            UserDto userDto = convertToDto(u);
            usersDto.add(userDto);
        }*/

        // return new ResponseEntity<Collection<UserDto>>(usersDTO, HttpStatus.OK);
        // return new ResponseEntity<>(users, HttpStatus.OK);
    // }

    /*
    private UserDto convertToDto(UserEntity user) {
        UserDto studentDto = modelMapper.map(user, UserDto.class);

        // Do something..
        studentDto.setImageId(student.getImage().getId());
        studentDto.setImagageType(student.getImage().getFileType());

        return studentDto;
    }

    private UserEntity convertToEntity(UserDto userDto) throws ParseException {
        UserEntity newUser = modelMapper.map(userDto, UserEntity.class);
        Optional<UserEntity> user = userRepository.findById(userDto.getId());
        if(!user.isPresent())
            return null;

        return user.get();
    }
    */

}