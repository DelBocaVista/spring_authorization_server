package com.example.AuthorizationServer.bo.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationTreeNodeDTO {

    Long id;
    String name;
    String path;
    Boolean enabled;
    List<OrganizationTreeNodeDTO> subOrganizations;

    public OrganizationTreeNodeDTO() {
        subOrganizations = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public List<OrganizationTreeNodeDTO> getSubOrganizations() { return subOrganizations; }
    public void setSubOrganizations(List<OrganizationTreeNodeDTO> subOrganizations) { this.subOrganizations = subOrganizations; }

    public void addSubOrganization(OrganizationTreeNodeDTO node) {
        this.subOrganizations.add(node);
    }
}
