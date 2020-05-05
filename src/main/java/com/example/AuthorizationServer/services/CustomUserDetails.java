package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private Collection<OrganizationDTO> organizations;

    public Collection<OrganizationDTO> getOrganizations() { return organizations; }
    public void setOrganizations(Collection<OrganizationDTO> organizations) { this.organizations = organizations; }

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Collection<OrganizationDTO> organizations) {
        super(username, password, authorities);
        this.organizations = organizations;
    }

}
