package com.example.AuthorizationServer.bo.dto;

import java.util.Set;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Data transfer object for UserEntity.
 */
public class UserEntityDTO {

    // Properties ---------------------------------------------------------------------------------
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String role;
    private Boolean enabled;
    private Set<OrganizationDTO> organizations;

    // Getters/setters ----------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Set<OrganizationDTO> getOrganizations() { return organizations; }
    public void setOrganizations(Set<OrganizationDTO> organizations) { this.organizations = organizations; }

    // Constructors -------------------------------------------------------------------------------
    public UserEntityDTO() {
    }

    public UserEntityDTO(Long id, String firstname, String lastname, String username, String role, Boolean enabled, Set<OrganizationDTO> organizations) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
        this.organizations = organizations;
    }
}
