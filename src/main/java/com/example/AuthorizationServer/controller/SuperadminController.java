package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.security.CustomUserDetails;
import com.example.AuthorizationServer.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for Organizations with role SUPERADMIN
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/superadmin")
public class SuperadminController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // String role = "ADMIN";

    @Autowired
    private OrganizationService orgService;

    /**
     * Retrieve all organisations
     *
     * @return the response entity
     */
    @GetMapping("/organization/")
    public ResponseEntity<?> getAllOrganizations() {
        List<OrganizationDTO> organizationDTOS = orgService.getAllOrganizations();
        return new ResponseEntity<>(organizationDTOS, HttpStatus.OK);
    }

    /**
     * Retrieve all organisations
     *
     * @return the response entity
     */
    @GetMapping("/organization/root/")
    public ResponseEntity<?> getAllRootOrganizations() {
        // Flytta funktionaliteten till OrganizationService !!!
        List<OrganizationDTO> organizationDTOS = orgService.getAllOrganizations();
        List<OrganizationDTO> result = new ArrayList<>();
        for (OrganizationDTO o: organizationDTOS) {
            String[] pathArray = o.getPath().split("\\.");
            if(pathArray.length == 1)
                result.add(o);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
