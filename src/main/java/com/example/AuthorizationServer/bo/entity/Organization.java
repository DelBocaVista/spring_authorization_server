package com.example.AuthorizationServer.bo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Represents an Organization in the booking system to which Users and Rooms can belong
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

    @OneToMany(mappedBy = "organization", cascade=CascadeType.ALL)
    private Set<UserEntity> userEntities = new HashSet<>();

    @Column(name="org_name")
    private String name;

    @Column(name="org_path")
    private String path;

    @Column(name="org_enabled", nullable = false)
    private Boolean enabled;

    // Getters/setters ----------------------------------------------------------------------------
    public Set<UserEntity> getUserEntities() { return userEntities; }
    public void setUserEntities(Set<UserEntity> userEntities) { this.userEntities = userEntities; }

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
    }

    public Organization(Set<UserEntity> userEntities, String name, Boolean enabled) {
        this.userEntities = userEntities;
        this.name = name;
        this.enabled = enabled;
    }

    public Organization(Set<UserEntity> userEntities, String name) {
        this.userEntities = userEntities;
        this.name = name;
        this.enabled = true;
    }

    public Organization(String name, String path) {
        this.name = name;
        this.path = path;
        this.enabled = true;
    }

    public void addUser(UserEntity userEntity) {
        userEntities.add(userEntity);
    }
}
