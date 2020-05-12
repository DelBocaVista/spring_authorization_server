package com.example.AuthorizationServer.security;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Custom subclass that models core user information retrieved by a UserDetailsService.
 */
public class CustomUserDetails extends User {

    // Properties ---------------------------------------------------------------------------------
    private Long id;
    private Collection<OrganizationDTO> organizations;

    // Getters/setters ----------------------------------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Collection<OrganizationDTO> getOrganizations() { return organizations; }
    public void setOrganizations(Collection<OrganizationDTO> organizations) { this.organizations = organizations; }

    // Constructors -------------------------------------------------------------------------------
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, Collection<OrganizationDTO> organizations) {
        super(username, password, authorities);
        this.id = id;
        this.organizations = organizations;
    }

}
