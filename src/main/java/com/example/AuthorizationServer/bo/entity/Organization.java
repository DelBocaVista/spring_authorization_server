package com.example.AuthorizationServer.bo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Represents an organization in the booking system to which users and rooms can belong.
 */
@Entity
@Table(name = "organizations")
public class Organization implements Serializable {
    // Constants ----------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Properties ---------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="org_id", updatable=false)
    private Long id;

    @ManyToMany(mappedBy = "organizations", cascade=CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    @Column(name="org_name", unique = true)
    private String name;

    @Column(name="org_path")
    private String path;

    @Column(name="org_enabled", nullable = false)
    private Boolean enabled;

    // Getters/setters ----------------------------------------------------------------------------
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return this.path; }
    public void setPath(String path) { this.path = path; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Constructors -------------------------------------------------------------------------------
    public Organization() {
        this.path = "";
    }

    public Organization(Set<User> users, String name, Boolean enabled) {
        this.users = users;
        this.name = name;
        this.enabled = enabled;
    }

    public Organization(Set<User> users, String name) {
        this.users = users;
        this.name = name;
        this.enabled = true;
    }

    public Organization(String name, Organization parent) {
        this.name = name;
        this.path = parent.path + "." + getId();
        this.enabled = true;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void setParent(Organization org) {
        if(org.getPath().equals(""))
            this.setPath(this.getId().toString());
        else
            this.setPath(org.path + "." + this.getId());
    }
}
