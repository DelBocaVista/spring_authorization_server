package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.repositories.OrganizationRepository;
import com.example.AuthorizationServer.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public String getPathByName(String name) {
        Organization org = organizationRepository.findByName(name);
        return org.getPath();
    }

    public Organization addOrganization(Organization org) {
        return organizationRepository.save(org);
    }
}
