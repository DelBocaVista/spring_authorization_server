package com.example.AuthorizationServer.repositories;

import com.example.AuthorizationServer.bo.entity.Organization;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface OrganizationRepositoryCustom {

    //public void someCustomMethod(User user);
    String getPath(Long id);

    // List<Organization> getParents(Long id);
}