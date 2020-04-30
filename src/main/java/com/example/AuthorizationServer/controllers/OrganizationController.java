package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNode;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.services.OrganizationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    /*
        Get all organizations
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllOrganizations() {
        List<Organization> organizations = orgService.getAll();
        List<OrganizationDTO> organizationDtos = new ArrayList<>();
        for (Organization o: organizations) {
            organizationDtos.add(convertToDto(o));
        }
        return new ResponseEntity<>(organizationDtos, HttpStatus.OK);
    }

    /*
        Get organization tree
     */
    @GetMapping("/tree/")
    public ResponseEntity<?> getOrganizationTree() {
        List<Organization> organizations = orgService.getAll();
        List<OrganizationTreeNode> nodes = new ArrayList<>();

        for (Organization o: organizations) {
            OrganizationTreeNode n = new OrganizationTreeNode();
            n.setId(o.getId());
            n.setName(o.getName());
            n.setPath(o.getPath());
            n.setEnabled(o.getEnabled());
            nodes.add(n);
        }

        List<OrganizationTreeNode> tree = buildTree(nodes);

        return new ResponseEntity<>(tree, HttpStatus.OK);
    }

    /*
        Get single organization by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        logger.info("Fetching Organization with id {}", id);
        Organization organization = orgService.getOrganizationById(id);
        if (organization == null) {
            logger.error("Organization with id {} not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        OrganizationDTO organizationDto = convertToDto(organization);

        return new ResponseEntity<>(organizationDto, HttpStatus.OK);
    }

    @PostMapping("/")
    public OrganizationDTO addOrganization(@RequestBody OrganizationDTO organizationDto) {
        Organization organization = convertToEntity(organizationDto);
        Organization organizationInDb = orgService.addOrganization(organization);
        return convertToDto(organizationInDb);
    }

    @PostMapping("/{parentId}")
    public OrganizationDTO addOrganizationToParent(@RequestBody OrganizationDTO organizationDto, @PathVariable Long parentId) {
        Organization parent = orgService.getOrganizationById(parentId);
        Organization organization = convertToEntity(organizationDto);
        Organization organizationInDb = orgService.addParentToOrganization(organization, parent);
        return convertToDto(organizationInDb);
    }

    @PutMapping("/{id}")
    public OrganizationDTO updateOrganization(@RequestBody OrganizationDTO organizationDto, @PathVariable Long id) {
        Organization organizationInDb = orgService.getOrganizationById(organizationDto.getId());
        Organization organization = convertToEntity(organizationDto);
        organization.setUserEntities(organizationInDb.getUserEntities());
        Organization updatedOrganization = orgService.updateOrganization(id, organization);
        return convertToDto(updatedOrganization);
    }

    @PutMapping("/changeParent/{childId}/{parentId}")
    public OrganizationDTO updateOrganizationParent(@PathVariable Long childId, @PathVariable Long parentId) {
        Organization child = orgService.getOrganizationById(childId);
        Organization parent = orgService.getOrganizationById(parentId);
        Organization updatedOrganization = orgService.addParentToOrganization(child, parent);
        return convertToDto(updatedOrganization);
    }

    @GetMapping("/children/all/{id}")
    public List<OrganizationDTO> getAllChildrenOfOrganization(@PathVariable Long id) {
        Organization organization = orgService.getOrganizationById(id);
        List<OrganizationDTO> organizationDtos = new ArrayList<>();
        List<Organization> organizations = orgService.getAllChildrenOfOrganization(organization);
        for (Organization o: organizations) {
            organizationDtos.add(convertToDto(o));
        }
        return organizationDtos;
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        orgService.deleteOrganization(id);
    }

    /**
     * Convert organization to organization dto
     * @param organization the organization to convert
     * @return the corresponding user entity dto
     */
    private OrganizationDTO convertToDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);

        // Do something else if needed..?

        return organizationDTO;
    }

    /**
     * Convert user entity dto to user entity
     * @param organizationDto the user entity dto to convert
     * @return the corresponding user entity
     */
    private Organization convertToEntity(OrganizationDTO organizationDto) throws ParseException {
        Organization organization = modelMapper.map(organizationDto, Organization.class);

        // Do something else if needed..?

        return organization;
    }

    private static List<OrganizationTreeNode> buildTree(List<OrganizationTreeNode> nodes) {
        HashMap<String, OrganizationTreeNode> map = new HashMap<>();
        for (OrganizationTreeNode n: nodes) {
            map.put(n.getId().toString(), n);
        }
        List<OrganizationTreeNode> tree = new ArrayList<>();
        for (OrganizationTreeNode n: nodes) {
            String[] path = n.getPath().split("\\.");
            if (path.length == 1) {
                tree.add(n);
            } else {
                // find nearest parent
                OrganizationTreeNode parent = map.get(path[path.length - 2]);
                // add self as child
                parent.addSubOrganization(n);
            }
        }
        return tree;
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
