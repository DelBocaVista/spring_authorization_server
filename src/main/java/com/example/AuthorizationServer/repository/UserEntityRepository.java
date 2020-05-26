package com.example.AuthorizationServer.repository;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Repository for persisting user entities.
 */
@Repository
@Transactional
public interface UserEntityRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findByUsernameAndEnabled(String userName, boolean enabled);

    Optional<UserEntity> findByUsernameAndRoleAndEnabled(String userName, String role, boolean enabled);

    List<UserEntity> findAllByRoleAndEnabled(String role, boolean enabled);

    Optional<UserEntity> findByRoleAndId(String role, Long id);

    Optional<UserEntity> findById(Long id);

    List<UserEntity> findAllByOrganizationsContainsAndRoleAndEnabled(Organization organization, String role, boolean enabled);

    void deleteByRoleAndId(String role, Long id);
}