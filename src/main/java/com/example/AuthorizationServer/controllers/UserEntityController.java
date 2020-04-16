package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SuppressWarnings("Duplicates")
@CrossOrigin
@RestController
@RequestMapping(path="/user")
public class UserEntityController {

    private static final Logger logger = LoggerFactory.getLogger(UserEntityController.class);

    String role = "USER";

    @Autowired
    private UserService userService;

    // @Autowired
    // private OrganizationInfoService orgService;

    /*
        Get all users
     */
    @GetMapping("/")
    public Object getAllUsers() {
        List<UserEntity> userEntities = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        if (userEntities == null || userEntities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    /*
        Get single student by id
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