package com.example.AuthorizationServer.repository;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Repository for persisting users.
 */
@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsernameAndEnabled(String userName, boolean enabled);

    Optional<User> findByUsernameAndRoleAndEnabled(String userName, String role, boolean enabled);

    List<User> findAllByRoleAndEnabled(String role, boolean enabled);

    Optional<User> findByRoleAndId(String role, Long id);

    Optional<User> findById(Long id);

    List<User> findAllByOrganizationsContainsAndRoleAndEnabled(Organization organization, String role, boolean enabled);

    void deleteByRoleAndId(String role, Long id);
}