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

@SuppressWarnings("Duplicates")
@CrossOrigin
@RestController
@RequestMapping(path="/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(UserEntityController.class);

    String role = "ADMIN";

    @Autowired
    private UserService userService;

    // @Autowired
    // private OrganizationInfoService orgService;

    @GetMapping("/")
    public Object getAllAdmins() {
        List<UserEntity> userEntities = userService.getAllActiveUsersByRole(role, SecurityContextHolder.getContext().getAuthentication());
        if (userEntities == null || userEntities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    // ADD MORE FUNTIONALITY HERE!!
}
