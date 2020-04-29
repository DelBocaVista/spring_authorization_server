package com.example.AuthorizationServer.controllers;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
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

    
}
