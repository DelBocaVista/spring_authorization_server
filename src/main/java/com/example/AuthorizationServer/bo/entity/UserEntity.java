package com.example.AuthorizationServer.bo.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Represents a User of the booking system. The name UserEntity is used instead of User due to a naming conflict with
 * the UserDetailsService class User
 */
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {

    // Constants ----------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Properties ---------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", updatable=false)
    private Long id;

    @Column(name="user_firstname")
    private String firstname;

    @Column(name="user_surname")
    private String lastname;

    @Column(name = "user_username", unique = true)
    private String username;

    @Column(name = "user_password", length = 800)
    private String password;

    @Column(name = "role") // Enum later?
    private String role;

    @Column(name="user_enabled", nullable = false, columnDefinition = "boolean default true")
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;

    // Getters/setters ----------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String name) { this.firstname = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization org) { this.organization = org; }

    public String getUsername() { return username;}
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Constructors -------------------------------------------------------------------------------
    public UserEntity() { }

    public UserEntity(String firstname, String lastname, String username, String password, String role, Organization organization) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.role = role;
        this.organization = organization;
        this.enabled = true;
    }

    public UserEntity(String username, String password) { // Initial for testing only
        this.username = username;
        this.password = password;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", organization=" + organization +
                '}';
    }
}