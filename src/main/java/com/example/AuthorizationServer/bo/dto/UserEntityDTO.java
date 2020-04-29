package com.example.AuthorizationServer.bo.dto;

import com.example.AuthorizationServer.bo.entity.Organization;

public class UserEntityDTO {

    // Properties ---------------------------------------------------------------------------------
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String role;
    private Boolean enabled;
    private Organization organization;

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

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    // Constructors -------------------------------------------------------------------------------
    public UserEntityDTO() {
    }

    public UserEntityDTO(Long id, String firstname, String lastname, String username, String role, Boolean enabled, Organization organization) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
        this.organization = organization;
    }
}
