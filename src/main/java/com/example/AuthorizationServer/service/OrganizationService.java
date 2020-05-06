package com.example.AuthorizationServer.service;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.controller.UserController;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Service for handling retrieving, saving and updating organizations.
 */
@Repository // Remove?
@Transactional // Remove?
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public OrganizationDTO getOrganizationDTOById(Long id) {
        return convertToDto(this.getOrganizationById(id));
    }

    // JUST FOR SEEDING - REMOVE LATER!!
    public Organization getOrganizationByIdSeed(Long id) {
        return this.getOrganizationById(id);
    }

    private Organization getOrganizationById(Long id) {
        logger.info("Fetching Organization with id {}", id);
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent()) {
            logger.error("Organization with id {} not found.", id);
            throw new NoSuchElementException();
        }
        return optionalOrg.get();
    }

    public OrganizationDTO getOrganizationByName(String name) {
        Optional<Organization> optionalOrg = organizationRepository.findByName(name);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return convertToDto(optionalOrg.get());
    }

    public String getPathByName(String name) {
        Optional<Organization> optionalOrg = organizationRepository.findByName(name);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return optionalOrg.get().getPath();
    }

    public OrganizationDTO addOrganization(OrganizationDTO org) {
        Organization returnedOrg = organizationRepository.save(convertToEntity(org));
        returnedOrg.setPath("");
        Optional<Organization> optionalOrg = organizationRepository.findByName(returnedOrg.getName());
        Organization orgInDb = optionalOrg.get();
        orgInDb.setParent(orgInDb);
        return convertToDto(organizationRepository.save(orgInDb));
    }

    // JUST FOR SEEDING - REMOVE LATER!!
    public Organization addOrganizationSeed(Organization org) {
        Organization returnedOrg = organizationRepository.save(org);
        returnedOrg.setPath("");
        Optional<Organization> optionalOrg = organizationRepository.findByName(returnedOrg.getName());
        Organization orgInDb = optionalOrg.get();
        orgInDb.setParent(orgInDb);
        return organizationRepository.save(orgInDb);
    }

    public OrganizationDTO addParentToOrganization(Long childId, Long parentId) {
        Organization child = this.getOrganizationById(childId);
        Organization parent = this.getOrganizationById(parentId);
        return this.addParentToOrganization(child, parent);
    }

    public OrganizationDTO addParentToOrganization(OrganizationDTO childDto, Long parentId) {
        Organization parent = this.getOrganizationById(parentId);
        Organization child = convertToEntity(childDto);
        return this.addParentToOrganization(child, parent);
    }

    public OrganizationDTO addParentToOrganization(Organization child, Organization parent) {
        Optional<Organization> optionalChild = organizationRepository.findByName(child.getName());
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalChild.isPresent() || !optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization childInDb = optionalChild.get();
        Organization parentInDb = optionalParent.get();
        childInDb.setParent(parentInDb);
        return convertToDto(organizationRepository.save(childInDb));
    }

    public List<OrganizationDTO> getAllChildrenOfOrganization(Long parentId) {
        Optional<Organization> optionalParent = organizationRepository.findById(parentId);
        if (!optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization parentInDB = optionalParent.get();
        List<Organization> orgs = organizationRepository.findByPathStartsWith(parentInDB.getPath());
        if(orgs.contains(parentInDB))
            orgs.remove(parentInDB);

        List<OrganizationDTO> orgDtos = new ArrayList<>();
        for (Organization o: orgs) {
            orgDtos.add(convertToDto(o));
        }
        return orgDtos;
    }

    public List<OrganizationDTO> getDirectChildrenOfOrganization(OrganizationDTO parent) {
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization parentInDB = optionalParent.get();
        List<Organization> orgs = organizationRepository.findByPathContains(parentInDB.getId().toString());
        List<OrganizationDTO> result = new ArrayList<>();
        for (Organization o: orgs) {
            String[] path = o.getPath().split("\\.");
            if (path.length == 2)
                result.add(convertToDto(o));
        }
        return result;
    }

    public List<OrganizationDTO> getAllOrganizations() {
        return this.findAll();
    }

    private List<OrganizationDTO> findAll() {
        List<OrganizationDTO> orgDtos = new ArrayList<>();
        List<Organization> orgs = organizationRepository.findAllByOrderByPathAsc();
        for (Organization o: orgs) {
            orgDtos.add(convertToDto(o));
        }

        return orgDtos;
    }

    public List<OrganizationTreeNodeDTO> getOrganizationSubTree(Long id) {
        List<Organization> organizations = organizationRepository.findByPathContainsOrderByPathAsc(id.toString());
        List<OrganizationDTO> organizationDTOS = new ArrayList<>();

        // Because of ascending sorting, the first item in organizations will be node with given id
        String[] pathArray = organizations.get(0).getPath().split("\\.");
        for (Organization o: organizations) {
            OrganizationDTO dto = convertToDto(o);
            if(pathArray.length > 1) {
                // Path does not start with given id
                String path = o.getPath();
                String match = "." + id + ".";
                if(o.getId().equals(id))
                    match = "." + id;
                String newPath = path.substring(path.indexOf(match));
                dto.setPath(newPath.substring(1));
                organizationDTOS.add(dto);
            } else {
                organizationDTOS.add(convertToDto(o));
            }
        }

        List<OrganizationTreeNodeDTO> nodes = convertToTreeNodeDto(organizationDTOS);

        return buildTree(nodes);
    }

    public List<OrganizationTreeNodeDTO> getFullOrganizationTree() {
        List<OrganizationDTO> organizations = this.findAll();
        List<OrganizationTreeNodeDTO> nodes = convertToTreeNodeDto(organizations);

        return buildTree(nodes);
    }

    private List<OrganizationTreeNodeDTO> convertToTreeNodeDto(List<OrganizationDTO> organizations) {
        List<OrganizationTreeNodeDTO> nodes = new ArrayList<>();

        for (OrganizationDTO o: organizations) {
            OrganizationTreeNodeDTO n = new OrganizationTreeNodeDTO();
            n.setId(o.getId());
            n.setName(o.getName());
            n.setPath(o.getPath());
            n.setEnabled(o.getEnabled());
            nodes.add(n);
        }
        return nodes;
    }

    public void changeParentOfOrganization(Long id, Long newParentId) {

        // NEEDS TO CHECK IF BOTH id AND newParentId are in the same organization!!!

        // Get organization and the new parent
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException();

        Optional<Organization> optionalNewParent = organizationRepository.findById(newParentId);
        if (!optionalNewParent.isPresent())
            throw new NoSuchElementException();

        Organization org = optionalOrg.get();
        Organization newParent = optionalNewParent.get();

        // Previous path of the organization and current parent
        String[] orgPath = org.getPath().split("\\.");

        if (orgPath.length <= 1)
            throw new IllegalArgumentException("Root organizations can not be changed into sub organizations");

        // Get all organizations whom are related to the organization
        List<Organization> allRelated = organizationRepository.findByPathContains(org.getId().toString());

        // organization already has a parent - replace path of parent with path of new parent
        String prevFullPath = org.getPath();
        String prevParentsOfOrg = prevFullPath.substring(0,prevFullPath.indexOf("." + org.getId()));
        System.out.println("prevP " + prevParentsOfOrg);

        for (Organization o : allRelated) {
            String s = o.getId() + " " + o.getPath();
            o.setPath(o.getPath().replace(prevParentsOfOrg, newParent.getPath()));
            System.out.println(s + " " + o.getPath());
        }

        organizationRepository.saveAll(allRelated);
    }

    public OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO) {
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        Organization updatedOrganization = optionalOrg.get();
        updatedOrganization.setName(organizationDTO.getName());
        updatedOrganization.setEnabled(organizationDTO.getEnabled());
        return convertToDto(organizationRepository.save(updatedOrganization));
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public String prettyPrint(List<OrganizationDTO> nodes) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nodes.size(); i++) {

            String[] path = nodes.get(i).getPath().split("\\.");

            for (int j = 0; j < path.length - 1; j++) {
                sb.append("  ");
            }
            sb.append(path[path.length - 1] + "\n");
        }
        return sb.toString();
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
                // find nearest parent
                OrganizationTreeNodeDTO parent = map.get(path[path.length - 2]);
                // add self as child
                parent.addSubOrganization(n);
            }
        }
        return tree;
    }
}
