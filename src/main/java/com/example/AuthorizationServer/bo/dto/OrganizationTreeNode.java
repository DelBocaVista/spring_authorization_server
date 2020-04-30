package com.example.AuthorizationServer.bo.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationTreeNode {

    Long id;
    String name;
    String path;
    Boolean enabled;
    List<OrganizationTreeNode> subOrganizations;

    public OrganizationTreeNode() {
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

    public List<OrganizationTreeNode> getSubOrganizations() { return subOrganizations; }
    public void setSubOrganizations(List<OrganizationTreeNode> subOrganizations) { this.subOrganizations = subOrganizations; }

    public void addSubOrganization(OrganizationTreeNode node) {
        this.subOrganizations.add(node);
    }
}
