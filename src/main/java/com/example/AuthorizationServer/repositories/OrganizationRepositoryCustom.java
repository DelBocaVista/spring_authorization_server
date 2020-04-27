package com.example.AuthorizationServer.repositories;

import org.springframework.data.repository.query.Param;

interface OrganizationRepositoryCustom {

    //public void someCustomMethod(User user);
    String getPath(Long id);

}