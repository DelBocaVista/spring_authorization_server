package com.example.AuthorizationServer.bo.dto;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Data transfer object for Organization.
 */
public class OrganizationDTO {

    // Properties ---------------------------------------------------------------------------------
    private Long id;
    private String name;
    private String path;
    private Boolean enabled;

    // Getters/setters ----------------------------------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    // Constructors -------------------------------------------------------------------------------
    public OrganizationDTO() {
    }

    public OrganizationDTO(Long id, String name, String path, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.enabled = enabled;
    }
}