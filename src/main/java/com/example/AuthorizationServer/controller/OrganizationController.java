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
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for organization. Used by both ADMIN and SUPERADMIN.
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/organizations")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private OrganizationService orgService;

    /**
     * Retrieve all organisations
     *
     * @return the response entity
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
     * Retrieve all root organisations
     *
     * @return the response entity
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
     * Retrieve the full organization tree
     *
     * @return the response entity
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
     * Retrieve the organization sub tree of given organization
     *
     * @return the response entity
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
     * Get a single organization by id
     *
     * @param id the organization id
     * @return the response entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));

        boolean authorized = false;

        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {

            // Superadmin is only authorized to retrieve root organizations
            if(orgService.isRootOrganization(id))
                authorized = true;

        } else {

            // Admin is only authorized to retrieve organizations within its own root organization sub tree
            
        }



        OrganizationDTO organizationDTO;
        try {
            organizationDTO = orgService.getOrganizationDTOById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(organizationDTO, HttpStatus.OK);
    }

    /**
     * Add a new root organization.
     * @param organizationDTO the dto of the organization to be created.
     * @return the response entity.
     */
    @PostMapping("/")
    public ResponseEntity<?> createRootOrganization(@RequestBody OrganizationDTO organizationDTO) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));
        if(!user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        OrganizationDTO addedOrganization = orgService.addOrganization(organizationDTO);
        return new ResponseEntity<>(addedOrganization, HttpStatus.OK);
    }

    /**
     * Add a new organization to a given parent.
     * @param organizationDto the dto of the organization to be created.
     * @param parentId the id of the intended parent.
     * @return the response entity.
     */
    @PostMapping("/{parentId}")
    public ResponseEntity<?> createSubOrganizationToParent(@RequestBody OrganizationDTO organizationDto, @PathVariable Long parentId) {
        CustomUserDetails user = UserDetailExtractor.extract((SecurityContextHolder.getContext()));
        // Resource is not allowed for superadmin
        if(user.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        // User must be admin - check if they have authority over root parent of given organization
        try {
            // Get root parent to given parentId
            OrganizationDTO rootParent = orgService.getRootParentOfOrganization(parentId);
            // Check if admins organization is the same as root parent
            for (OrganizationDTO o: user.getOrganizations()) {
                if (o.getId().equals(rootParent.getId())) {
                    // Admin has authority in organization tree - create suborganization
                    OrganizationDTO orgInDb = orgService.addParentToOrganization(organizationDto, parentId);
                    return new ResponseEntity<>(orgInDb, HttpStatus.OK);
                }
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     *
     * @param organizationDTO
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganization(@RequestBody OrganizationDTO organizationDTO, @PathVariable Long id) {
        CustomUserDetails userDetails = UserDetailExtractor.extract(SecurityContextHolder.getContext());
        OrganizationDTO rootParent;
        boolean authorized = false;
        if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("SUPERADMIN"))) {
            // Superadmin users are only allowed to update root organizations
            rootParent = orgService.getRootParentOfOrganization(id);
            if(!rootParent.getId().equals(id))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            else
                authorized = true;
        } else {
            // Admin users are only allowed to update suborganizations of own root organization
            rootParent = orgService.getRootParentOfOrganization(id);
            for (OrganizationDTO o: userDetails.getOrganizations()) {
                if(rootParent.getId().equals(o.getId()))
                    authorized = true;
            }
        }

        if(authorized) {
            OrganizationDTO updatedOrganizationDTO = orgService.updateOrganization(id, organizationDTO);
            return new ResponseEntity<>(updatedOrganizationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{childId}/changeParent/{parentId}")
    public OrganizationDTO updateOrganizationParent(@PathVariable Long childId, @PathVariable Long parentId) {
        return orgService.addParentToOrganization(childId, parentId);
    }

    @GetMapping("{id}/children/")
    public List<OrganizationDTO> getAllChildrenOfOrganization(@PathVariable Long id) {
        return orgService.getAllChildrenOfOrganization(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        orgService.deleteOrganization(id);
    }



    /*private static List<TreeNode> buildTree(List<TreeNode> nodes) {
        HashMap<String, TreeNode> map = new HashMap<>();
        for (TreeNode n: nodes) {
            map.put(n.id.toString(), n);
        }
        List<TreeNode> tree = new ArrayList<>();
        for (TreeNode n: nodes) {
            String[] path = n.path.split("\\.");
            if (path.length == 1) {
                tree.add(n);
            } else {
                // find nearest parent
                TreeNode parent = map.get(path[path.length - 2]);
                // add self as child
                parent.subOrganizations.add(n);
            }
        }
        return tree;
    }

    private class TreeNode {

        Long id;
        String name;
        String path;
        Boolean enabled;
        List<TreeNode> subOrganizations;

        TreeNode() {
            subOrganizations = new ArrayList<>();
        }
    }*/

}
