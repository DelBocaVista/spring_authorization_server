package com.example.AuthorizationServer.repositories;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserEntityRepository extends CrudRepository<UserEntity, String> {

    UserEntity findByUsernameAndEnabled(String userName, boolean enabled);

    /*List<UserEntity> findAllByEnabled(boolean enabled);

    Optional<UserEntity> findById(Long id);

    void deleteById(Long id);*/

    // Extended handling

    UserEntity findByUsernameAndRoleAndEnabled(String userName, String role, boolean enabled);

    List<UserEntity> findAllByRoleAndEnabled(String role, boolean enabled);

    Optional<UserEntity> findByRoleAndId(String role, Long id);

    void deleteByRoleAndId(String role, Long id);
}