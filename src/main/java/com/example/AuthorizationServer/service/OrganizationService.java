package com.example.AuthorizationServer.service;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.controller.UserController;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import com.example.AuthorizationServer.utility.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se), Erik Wikzén (wikzen@kth.se)
 *
 * Service for handling retrieving, saving and updating organizations.
 */
@Service
@Transactional
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final OrganizationRepository organizationRepository;

    private final MapperUtil mapperUtil;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, MapperUtil mapperUtil) {
        this.organizationRepository = organizationRepository;
        this.mapperUtil = mapperUtil;
    }

    /**
     * Fetches an organization dto from id.
     *
     * @param id the id of the organization dto.
     * @return the organization dto.
     */
    public OrganizationDTO getOrganizationById(Long id) {
        return mapperUtil.convertToDto(this.getOrganizationEntityById(id));
    }

    /**
     * Fetches an organization dto from name.
     * @param name the name of the organization dto.
     * @return the organization dto.
     */
    public OrganizationDTO getOrganizationByName(String name) {
        Optional<Organization> optionalOrg = organizationRepository.findByName(name);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return mapperUtil.convertToDto(optionalOrg.get());
    }

    /**
     * Create a new organization.
     *
     * @param org the organization dto of the organization.
     * @return the organization dto representing the new organization.
     */
    public OrganizationDTO addOrganization(OrganizationDTO org) {
        Organization returnedOrg = organizationRepository.save(mapperUtil.convertToEntity(org));
        returnedOrg.setPath("");
        Optional<Organization> optionalOrg = organizationRepository.findByName(returnedOrg.getName());
        Organization orgInDb = optionalOrg.get();
        orgInDb.setParent(orgInDb);
        return mapperUtil.convertToDto(organizationRepository.save(orgInDb));
    }

    /**
     * Sets an organization as the parent of another organization.
     *
     * @param childId the id of the intended child organization.
     * @param parentId the id of the intended parent organization.
     * @return the organization dto representing the updated child organization.
     */
    public OrganizationDTO addParentToOrganization(Long childId, Long parentId) {
        Organization child = this.getOrganizationEntityById(childId);
        Organization parent = this.getOrganizationEntityById(parentId);
        return this.addParentToOrganization(child, parent);
    }

    /**
     * Sets an organization as the parent of another organization.
     *
     * @param childDto the dto version of the intended child organization.
     * @param parentId the id of the intended parent organization.
     * @return the organization dto representing the updated child organization.
     */
    public OrganizationDTO addParentToOrganization(OrganizationDTO childDto, Long parentId) {
        Organization parent = this.getOrganizationEntityById(parentId);
        Organization child = mapperUtil.convertToEntity(childDto);
        Organization childInDb = organizationRepository.save(child);
        return this.addParentToOrganization(childInDb, parent);
    }

    /**
     * Sets an organization as the parent of another organization.
     *
     * @param child the intended child organization.
     * @param parent the intended parent organization.
     * @return the organization dto representing the updated child organization.
     */
    public OrganizationDTO addParentToOrganization(Organization child, Organization parent) {
        Optional<Organization> optionalChild = organizationRepository.findByName(child.getName());
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalChild.isPresent() || !optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization childInDb = optionalChild.get();
        Organization parentInDb = optionalParent.get();
        childInDb.setParent(parentInDb);
        return mapperUtil.convertToDto(organizationRepository.save(childInDb));
    }

    /**
     * Fetches all the sub organizations of a given parent.
     *
     * @param parentId the id of the parent organization.
     * @return the sub organizations of the parent.
     */
    public List<OrganizationDTO> getAllChildrenOfOrganization(Long parentId) {
        Optional<Organization> optionalParent = organizationRepository.findById(parentId);
        if (!optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization parentInDB = optionalParent.get();

        List<Organization> orgs = organizationRepository.findByPathStartsWith(parentInDB.getPath());
        orgs.remove(parentInDB);

        List<OrganizationDTO> orgDtos = new ArrayList<>();
        for (Organization o: orgs) {
            orgDtos.add(mapperUtil.convertToDto(o));
        }
        return orgDtos;
    }

    /**
     * Fetches all organizations.
     *
     * @return the organizations found.
     */
    public List<OrganizationDTO> getAllOrganizations() {
        return this.findAll();
    }

    /**
     * Checks if an organization is a child of another organization.
     *
     * @param childId the id of the child organization.
     * @param parentId the id of the parent organization.
     * @return true if the organization is a child of the parent otherwise false.
     */
    public boolean isOrganizationChildOfRootParent(Long childId, Long parentId) {
        Optional<Organization> optionalChild = organizationRepository.findById(childId);
        if(!optionalChild.isPresent())
            throw new NoSuchElementException();
        Optional<Organization> optionalRoot = organizationRepository.findById(parentId);
        if(!optionalRoot.isPresent())
            throw new NoSuchElementException();

        Organization child = optionalChild.get();
        Organization root = optionalRoot.get();

        String[] pathArray = child.getPath().split("\\.");

        for (String s:pathArray) {
            if(s.equals(root.getId().toString()))
                return true;
        }

        return false;
    }

    /**
     * Fetches all root organizations.
     *
     * @return the root organizations found.
     */
    public List<OrganizationDTO> getAllRootOrganizations() {
        List<OrganizationDTO> organizationDTOS = this.getAllOrganizations();
        List<OrganizationDTO> result = new ArrayList<>();
        for (OrganizationDTO o: organizationDTOS) {
            String[] pathArray = o.getPath().split("\\.");
            if(pathArray.length == 1)
                result.add(o);
        }
        return result;
    }

    /**
     * Checks if an organization is a root organization.
     *
     * @param id the id of the organization.
     * @return true if the organization is a root organization otherwise false.
     */
    public boolean isRootOrganization(Long id) {
        Optional<Organization> optionalOrganization = organizationRepository.findById(id);
        if(!optionalOrganization.isPresent())
            return false;

        Organization organization = optionalOrganization.get();
        String[] pathArray = organization.getPath().split("\\.");

        if(pathArray.length > 1)
            return false;

        return true;
    }

    /**
     * Fetches the root organization that a given organization belongs to.
     *
     * @param id the id of the organization.
     * @return the root organization.
     */
    public OrganizationDTO getRootParentOfOrganization(Long id) {
        Optional<Organization> optionalOrganization = organizationRepository.findById(id);
        if(!optionalOrganization.isPresent())
            throw new NoSuchElementException();
        Organization organization = optionalOrganization.get();
        String[] pathArray = organization.getPath().split("\\.");
        Optional<Organization> optionalRootParent = organizationRepository.findById(Long.valueOf(pathArray[0]));
        if(!optionalRootParent.isPresent())
            throw new NoSuchElementException();
        Organization rootParent = optionalRootParent.get();
        return mapperUtil.convertToDto(rootParent);
    }

    /**
     * Fetches the sub tree of organizations with a given organization as root.
     *
     * @param id the id of the sub tree root organization.
     * @return the sub tree represented as a sorted list.
     */
    public List<OrganizationTreeNodeDTO> getOrganizationSubTree(Long id) {

        List<Organization> organizations = organizationRepository.findByPathContainsOrderByPathAsc(id.toString());
        List<OrganizationDTO> organizationDTOS = new ArrayList<>();

        // Because of ascending sorting in list, the first item in organizations will be node with given id
        String[] pathArray = organizations.get(0).getPath().split("\\.");

        for (Organization o: organizations) {

            OrganizationDTO dto = mapperUtil.convertToDto(o);
            String[] orgPathArray = dto.getPath().split("\\.");

            // Result of the "contains" query could possibly contain unwanted organization
            if (pathArray.length > 1) {
                // Path contains more than one entry so it does not start with the given id..
                // ..therefore we need to trim the path so that it does

                String path = o.getPath();

                // Assume that the organization does not have the given id..
                String match = "." + id + ".";
                // ..but adjust match string if it does
                if (o.getId().equals(id))
                    match = "." + id;

                int matchPosition = path.indexOf(match);
                if (matchPosition > -1) {
                    // Match found so organization belongs in sub tree
                    String newPath = path.substring(matchPosition);
                    dto.setPath(newPath.substring(1));
                    organizationDTOS.add(dto);
                }
            } else {
                // Path starts with given id..
                // ..so organization belongs to sub tree if the root id matches the given id
                if (orgPathArray[0].equals(id.toString()))
                    organizationDTOS.add(dto);
            }
        }

        List<OrganizationTreeNodeDTO> nodes = mapperUtil.convertToTreeNodeDtos(organizationDTOS);

        return buildTree(nodes);
    }

    /**
     * Fetches the organization tree structures of all organization currently in the system.
     *
     * @return the tree structures.
     */
    public List<OrganizationTreeNodeDTO> getFullOrganizationTree() {
        List<OrganizationDTO> organizations = this.findAll();
        List<OrganizationTreeNodeDTO> nodes = mapperUtil.convertToTreeNodeDtos(organizations);

        return buildTree(nodes);
    }

    /**
     * Updates an existing organization.
     *
     * @param id the id of the organization to be updated.
     * @param organizationDTO the updated organization dto version of the organization to be updated.
     * @return the organization dto representing the updated organization.
     */
    public OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO) {
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        Organization updatedOrganization = optionalOrg.get();
        updatedOrganization.setName(organizationDTO.getName());
        updatedOrganization.setEnabled(organizationDTO.getEnabled());
        return mapperUtil.convertToDto(organizationRepository.save(updatedOrganization));
    }

    /**
     * Deletes an organization.
     *
     * @param id the id of the organization to be deleted.
     */
    public void deleteOrganization(Long id) {
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException();
        organizationRepository.deleteById(id);
    }

    /**
     * Finds all available organizations.
     *
     * @return the organizations found.
     */
    private List<OrganizationDTO> findAll() {
        List<OrganizationDTO> orgDtos = new ArrayList<>();
        List<Organization> orgs = organizationRepository.findAllByOrderByPathAsc();
        for (Organization o: orgs) {
            orgDtos.add(mapperUtil.convertToDto(o));
        }

        return orgDtos;
    }

    /**
     * Fetches an organization from id.
     *
     * @param id the id of the organization.
     * @return the organization.
     */
    private Organization getOrganizationEntityById(Long id) {
        logger.info("Fetching Organization with id {}", id);
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent()) {
            logger.error("Organization with id {} not found.", id);
            throw new NoSuchElementException();
        }
        return optionalOrg.get();
    }

    /**
     * Builds organization tree structures consisting of linked organization tree nodes.
     *
     * @param nodes the organization tree node dtos to build the tree structures from.
     * @return the tree structures.
     */
    private static List<OrganizationTreeNodeDTO> buildTree(List<OrganizationTreeNodeDTO> nodes) {

        HashMap<String, OrganizationTreeNodeDTO> map = new HashMap<>();

        for (OrganizationTreeNodeDTO n: nodes) {
            map.put(n.getId().toString(), n);
        }

        List<OrganizationTreeNodeDTO> tree = new ArrayList<>();

        for (OrganizationTreeNodeDTO n: nodes) {
            String[] path = n.getPath().split("\\.");
            if (path.length == 1) {
                tree.add(n);
            } else {
                // find nearest parent, i.e. the second to last organization id in the path
                OrganizationTreeNodeDTO parent = map.get(path[path.length - 2]);
                // add self as child
                parent.addSubOrganization(n);
            }
        }

        return tree;
    }
}
