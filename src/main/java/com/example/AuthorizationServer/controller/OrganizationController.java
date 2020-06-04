package com.example.AuthorizationServer.controller;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.security.CustomUserDetails;
import com.example.AuthorizationServer.service.OrganizationService;
import com.example.AuthorizationServer.utility.UserDetailExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se), Erik Wikzén (wikzen@kth.se)
 *
 * Controller for REST API requests for organizations. Both admin and superadmin roles has access to this
 * resource. General access is upheld through http security configuration in ResourceServerConfig. Any endpoint specific
 * access rules are implemented in their respective methods.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/organizations")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService orgService;

    @Autowired
    public OrganizationController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    /**
     * Retrieve all organisations.
     *
     * @return the response entity.
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllOrganizations() {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        // Only superadmin is authorized
        if(!user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<OrganizationDTO> organizationDTOS = orgService.getAllOrganizations();
        return new ResponseEntity<>(organizationDTOS, HttpStatus.OK);
    }

    /**
     * Retrieve all root organizations.
     *
     * @return the response entity.
     */
    @GetMapping("/roots/")
    public ResponseEntity<?> getAllRootOrganizations() {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        // Only superadmin is authorized
        if(!user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<OrganizationDTO> organizationDTOS = orgService.getAllRootOrganizations();
        return new ResponseEntity<>(organizationDTOS, HttpStatus.OK);
    }

    /**
     * Retrieve the full organization tree.
     *
     * @return the response entity.
     */
    @GetMapping("/trees/")
    public ResponseEntity<?> getFullOrganizationTree() {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        // Only superadmin is authorized
        if(!user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<OrganizationTreeNodeDTO> tree = orgService.getFullOrganizationTree();
        return new ResponseEntity<>(tree, HttpStatus.OK);
    }

    /**
     * Retrieve the organization sub tree of given organization.
     *
     * @return the response entity.
     */
    @GetMapping("/trees/{id}")
    public ResponseEntity<?> getOrganizationSubTree(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        boolean authorized = false;

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is always authorized
            authorized = true;
        } else {
            // Admin is authorized for organizations within its own root organization sub tree
            try {
                OrganizationDTO rootParent = orgService.getRootParentOfOrganization(id);
                for (OrganizationDTO o : user.getOrganizations()) {
                    if (o.getId().equals(rootParent.getId()))
                        authorized = true;
                }
            } catch (NoSuchElementException e) {
                return new ResponseEntity<>("Unexpected error. Organization not found.", HttpStatus.NOT_FOUND);
            }
        }

        if(authorized) {
            List<OrganizationTreeNodeDTO> tree = orgService.getOrganizationSubTree(id);
            return new ResponseEntity<>(tree, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Get a single organization by id.
     *
     * @param id the organization id.
     * @return the response entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganizationById(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        boolean authorized = false;

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is only authorized to retrieve root organizations
            if(orgService.isRootOrganization(id))
                authorized = true;
        } else {
            // Admin is only authorized to retrieve organizations within its own root organization sub tree
            for (OrganizationDTO o: user.getOrganizations()) {
                if(orgService.isOrganizationChildOfRootParent(id, o.getId()))
                    authorized = true;
            }
        }

        if (!authorized)
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);

        OrganizationDTO organizationDTO;
        try {
            organizationDTO = orgService.getOrganizationById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(organizationDTO, HttpStatus.OK);
    }

    /**
     * Add a new root organization.
     *
     * @param organizationDTO the dto of the organization to be created.
     * @return the response entity.
     */
    @PostMapping("/")
    public ResponseEntity<?> createRootOrganization(@RequestBody OrganizationDTO organizationDTO) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        if(!user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Only superadmin is allowed to create root organizations
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        OrganizationDTO addedOrganization = orgService.addOrganization(organizationDTO);
        return new ResponseEntity<>(addedOrganization, HttpStatus.OK);
    }

    /**
     * Add a new organization to a given parent.
     *
     * @param organizationDto the dto of the organization to be created.
     * @param parentId the id of the intended parent.
     * @return the response entity.
     */
    @PostMapping("/{parentId}")
    public ResponseEntity<?> createSubOrganizationToParent(@RequestBody OrganizationDTO organizationDto,
                                                           @PathVariable Long parentId) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is not allowed to create sub organizations
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
        }

        try {
            OrganizationDTO rootParent = orgService.getRootParentOfOrganization(parentId);
            // Admin is only authorized to create sub organizations in own root parent organization
            for (OrganizationDTO o: user.getOrganizations()) {
                if (o.getId().equals(rootParent.getId())) {
                    OrganizationDTO orgInDb = orgService.addParentToOrganization(organizationDto, parentId);
                    return new ResponseEntity<>(orgInDb, HttpStatus.OK);
                }
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Unexpected error. Organization not found.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Update an existing organization.
     *
     * @param organizationDTO the updated dto of the organization.
     * @param id the id of the organization.
     * @return the response entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganization(@RequestBody OrganizationDTO organizationDTO,
                                                @PathVariable Long id) {
        CustomUserDetails userDetails = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO rootParent;
        boolean authorized = false;

        if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is only allowed to update root organizations
            rootParent = orgService.getRootParentOfOrganization(id);

            if(!rootParent.getId().equals(id))
                return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
            else
                authorized = true;

        } else {
            rootParent = orgService.getRootParentOfOrganization(id);
            // Admin is only authorized to update organizations in its own organization sub tree
            for (OrganizationDTO o: userDetails.getOrganizations()) {
                if(rootParent.getId().equals(o.getId()))
                    authorized = true;
            }
        }

        if(authorized) {
            OrganizationDTO updatedOrganizationDTO = orgService.updateOrganization(id, organizationDTO);
            return new ResponseEntity<>(updatedOrganizationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Change the parent of an existing organization.
     *
     * @param childId the id of the organization changing parent organizations.
     * @param parentId the id of the new parent organization.
     * @return the response entity.
     */
    @PutMapping("/{childId}/parent/{parentId}")
    public ResponseEntity<?> updateOrganizationParent(@PathVariable Long childId, @PathVariable Long parentId) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is not allowed to create sub organizations
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
        }

        boolean childAuthorized = false;
        boolean parentAuthorized = false;
        // Admin is only authorized to update organizations in its own organization sub tree
        OrganizationDTO rootParent = orgService.getRootParentOfOrganization(childId);
        for (OrganizationDTO o: user.getOrganizations()) {
            if(rootParent.getId().equals(o.getId()))
                childAuthorized = true;
            if(o.getId().equals(parentId))
                parentAuthorized = true;
        }

        if(!childAuthorized || !parentAuthorized)
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);

        OrganizationDTO updatedOrganization = orgService.addParentToOrganization(childId, parentId);

        return new ResponseEntity<>(updatedOrganization, HttpStatus.OK);
    }

    /**
     * Get all the children of an organization.
     *
     * @param id the id of the organization.
     * @return the response entity.
     */
    @GetMapping("{id}/children/")
    public ResponseEntity<?> getAllChildrenOfOrganization(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is not allowed to fetch sub organizations
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
        }

        OrganizationDTO rootParent = orgService.getRootParentOfOrganization(id);
        // Admin is only authorized to view organizations in its own organization sub tree
        for (OrganizationDTO o: user.getOrganizations()) {
            if(rootParent.getId().equals(o.getId())) {
                List<OrganizationDTO> children = orgService.getAllChildrenOfOrganization(id);
                return new ResponseEntity<>(children, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Delete an organization.
     *
     * @param id the id of the organization to be deleted.
     * @return the response entity.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        CustomUserDetails userDetails = UserDetailExtractor.extract(SecurityContextHolder.getContext());

        OrganizationDTO rootParent;
        boolean authorized = false;

        if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin is only allowed to delete root organizations
            rootParent = orgService.getRootParentOfOrganization(id);

            if(!rootParent.getId().equals(id))
                return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
            else
                authorized = true;

        } else {
            rootParent = orgService.getRootParentOfOrganization(id);
            // Admin is only authorized to delete organizations in its own organization sub tree
            for (OrganizationDTO o: userDetails.getOrganizations()) {
                if(rootParent.getId().equals(o.getId()))
                    authorized = true;
            }
        }

        if(authorized) {

            try {
                orgService.deleteOrganization(id);
            } catch (Exception e) {
                return new ResponseEntity<>("Unexpected error. Organization not found.", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Successfully deleted organization with id " + id + ".", HttpStatus.OK);

        } else {
            return new ResponseEntity<>("Unexpected error. Not authorized.", HttpStatus.UNAUTHORIZED);
        }
    }
}
