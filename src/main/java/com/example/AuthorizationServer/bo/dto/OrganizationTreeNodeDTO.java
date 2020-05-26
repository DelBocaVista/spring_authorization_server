package com.example.AuthorizationServer.bo.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Subclass of OrganizationDTO primarily used for building organization tree JSON objects.
 */
public class OrganizationTreeNodeDTO extends OrganizationDTO {

    // Properties ---------------------------------------------------------------------------------
    List<OrganizationTreeNodeDTO> subOrganizations;

    // Getters/setters ----------------------------------------------------------------------------
    public List<OrganizationTreeNodeDTO> getSubOrganizations() { return subOrganizations; }
    public void setSubOrganizations(List<OrganizationTreeNodeDTO> subOrganizations) { this.subOrganizations = subOrganizations; }

    // Constructors -------------------------------------------------------------------------------
    public OrganizationTreeNodeDTO() {
        super();
        subOrganizations = new ArrayList<>();
    }

    public OrganizationTreeNodeDTO(Long id, String name, String path, Boolean enabled, List<OrganizationTreeNodeDTO> subOrganizations) {
        super(id, name, path, enabled);
        this.subOrganizations = subOrganizations;
    }

    /**
     * Adds a sub organization node
     * @param node the sub organization node
     */
    public void addSubOrganization(OrganizationTreeNodeDTO node) {
        this.subOrganizations.add(node);
    }
}
