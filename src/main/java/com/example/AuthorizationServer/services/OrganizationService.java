package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Service for handling retrieving, saving and updating organizations
 */
@Repository // Remove?
@Transactional // Remove?
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public Organization getOrganizationById(Long id) {
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return optionalOrg.get();
    }

    public Organization getOrganizationByName(String name) {
        Optional<Organization> optionalOrg = organizationRepository.findByName(name);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return optionalOrg.get();
    }

    public String getPathByName(String name) {
        Optional<Organization> optionalOrg = organizationRepository.findByName(name);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        return optionalOrg.get().getPath();
    }

    public Organization addOrganization(Organization org) {
        Organization returnedOrg = organizationRepository.save(org);
        returnedOrg.setPath("");
        Optional<Organization> optionalOrg = organizationRepository.findByName(returnedOrg.getName());
        Organization orgInDb = optionalOrg.get();
        orgInDb.setParent(orgInDb);
        return organizationRepository.save(orgInDb);
    }

    public Organization addParentToOrganization(Organization child, Organization parent) {
        Optional<Organization> optionalChild = organizationRepository.findByName(child.getName());
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalChild.isPresent() || !optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization childInDb = optionalChild.get();
        Organization parentInDb = optionalParent.get();
        childInDb.setParent(parentInDb);
        return organizationRepository.save(childInDb);
    }

    public List<Organization> getAllChildrenOfOrganization(Organization parent) {
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization parentInDB = optionalParent.get();
        List<Organization> orgs = organizationRepository.findByPathStartsWith(parentInDB.getPath());
        if(orgs.contains(parentInDB))
            orgs.remove(parentInDB);
        return orgs;
    }

    public List<Organization> getDirectChildrenOfOrganization(Organization parent) {
        Optional<Organization> optionalParent = organizationRepository.findByName(parent.getName());
        if (!optionalParent.isPresent())
            throw new NoSuchElementException(); // ?
        Organization parentInDB = optionalParent.get();
        List<Organization> orgs = organizationRepository.findByPathContains(parentInDB.getId().toString());
        List<Organization> result = new ArrayList<>();
        for (Organization o: orgs) {
            String[] path = o.getPath().split("\\.");
            if (path.length == 2)
                result.add(o);
        }
        return result;
    }

    public List<Organization> getAll() {
        return organizationRepository.findAllByOrderByPathAsc();
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

    public Organization updateOrganization(Long id, Organization organization) {
        Optional<Organization> optionalOrg = organizationRepository.findById(id);
        if (!optionalOrg.isPresent())
            throw new NoSuchElementException(); // ?
        Organization updatedOrganization = optionalOrg.get();
        updatedOrganization.setName(organization.getName());
        updatedOrganization.setEnabled(organization.getEnabled());
        return organizationRepository.save(updatedOrganization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public String prettyPrint(List<Organization> nodes) {

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
}
