package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.services.OrganizationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Controller for REST API requests for UserEntity with role ADMIN
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(path="/organization")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // String role = "ADMIN";

    @Autowired
    private OrganizationService orgService;

    /*
        Get all organizations
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllOrganizations() {
        List<OrganizationDTO> organizationDTOS = orgService.getAllOrganizations();
        return new ResponseEntity<>(organizationDTOS, HttpStatus.OK);
    }

    /*
        Get organization tree
     */
    @GetMapping("/tree/")
    public ResponseEntity<?> getOrganizationTree() {
        List<OrganizationTreeNodeDTO> tree = orgService.buildOrganizationTree();
        return new ResponseEntity<>(tree, HttpStatus.OK);
    }

    /*
        Get single organization by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        OrganizationDTO organizationDTO;
        try {
            organizationDTO = orgService.getOrganizationDTOById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(organizationDTO, HttpStatus.OK);
    }

    @PostMapping("/")
    public OrganizationDTO addOrganization(@RequestBody OrganizationDTO organizationDto) {
        return orgService.addOrganization(organizationDto);
    }

    @PostMapping("/{parentId}")
    public OrganizationDTO addOrganizationToParent(@RequestBody OrganizationDTO organizationDto, @PathVariable Long parentId) {
        return orgService.addParentToOrganization(organizationDto, parentId);
    }

    @PutMapping("/{id}")
    public OrganizationDTO updateOrganization(@RequestBody OrganizationDTO organizationDto, @PathVariable Long id) {
        return orgService.updateOrganization(id, organizationDto);
    }

    @PutMapping("/changeParent/{childId}/{parentId}")
    public OrganizationDTO updateOrganizationParent(@PathVariable Long childId, @PathVariable Long parentId) {
        return orgService.addParentToOrganization(childId, parentId);
    }

    @GetMapping("/children/all/{id}")
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
