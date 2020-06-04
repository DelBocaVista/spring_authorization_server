package com.example.AuthorizationServer.bo.dto;

import java.util.Set;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Subclass of UserDTO containing an additional password property. Used for data transfer when attempting to add
 * or update a User.
 */
public class UserExtendedDTO extends UserDTO {

    // Properties ---------------------------------------------------------------------------------
    private String password;

    // Getters/setters ----------------------------------------------------------------------------
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Constructors -------------------------------------------------------------------------------
    public UserExtendedDTO() {
        super();
    }

    public UserExtendedDTO(Long id, String firstname, String lastname, String username, String password, String role, Boolean enabled, Set<OrganizationDTO> organizations) {
        super(id, firstname, lastname, username, role, enabled, organizations);
        this.password = password;
    }
}
