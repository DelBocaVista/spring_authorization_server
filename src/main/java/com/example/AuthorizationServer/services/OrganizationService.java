package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.repositories.OrganizationRepository;
import com.example.AuthorizationServer.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return organizationRepository.findById(id);
    }

    public Organization getOrganizationByName(String name) {
        return organizationRepository.findByName(name);
    }

    public String getPathByName(String name) {
        Organization org = organizationRepository.findByName(name);
        return org.getPath();
    }

    public Organization addOrganization(Organization org) {
        Organization retOrg = organizationRepository.save(org);
        retOrg.setPath("");
        Organization fullOrg = organizationRepository.findByName(retOrg.getName());
        fullOrg.setParent(fullOrg);
        return organizationRepository.save(fullOrg);
    }

    public Organization addParentToOrganization(Organization child, Organization parent) {
        Organization fullChild = organizationRepository.findByName(child.getName());
        Organization fullParent = organizationRepository.findByName(parent.getName());
        fullChild.setParent(fullParent);
        return organizationRepository.save(fullChild);
    }

    public List<Organization> getAllChildrenOfOrganization(Organization parent) {
        Organization parentInDB = organizationRepository.findByName(parent.getName());
        List<Organization> orgs = organizationRepository.findByPathStartsWith(parentInDB.getPath());
        if(orgs.contains(parentInDB))
            orgs.remove(parentInDB);
        return orgs;
    }
}
