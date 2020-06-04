package com.example.AuthorizationServer.bo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Represents a user of the booking system.
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Set<Organization> organizations = new HashSet<>();

    // Getters/setters ----------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String name) { this.firstname = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Set<Organization> getOrganizations() { return organizations; }
    public void setOrganizations(Set<Organization> orgs) { this.organizations = orgs; }

    public String getUsername() { return username;}
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Constructors -------------------------------------------------------------------------------
    public User() {
        this.organizations = new HashSet<>();
    }

    public User(String firstname, String lastname, String username, String password, String role, Set<Organization> organizations) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.role = role;
        this.organizations = organizations;
        this.enabled = true;
    }

    public User(String username, String password) { // Initial for testing only
        this.username = username;
        this.password = password;
    }

    // Actions ------------------------------------------------------------------------------------

    public void addOrganization(Organization org) {
        this.organizations.add(org);
    }

    public void removeOrganization(Organization org) {
        this.organizations.remove(org);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", organizations=" + organizations +
                '}';
    }
}