package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Service for handling retrieving, saving and updating user entitys
 */
@Repository // Remove?
@Transactional // Remove?
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    public UserEntity getUserByUsername(String username) {
        UserEntity user = userEntityRepository.findByUsernameAndEnabled(username,true);
        return user;
    }

    public UserEntity getUserByRoleAndUsername(String role, String username) {
        UserEntity user = userEntityRepository.findByUsernameAndRoleAndEnabled(username, role, true);
        return user;
    }

    public Optional<UserEntity> getUserByRoleAndId(String role, Long id) {
        return userEntityRepository.findByRoleAndId(role, id);
    }

    public List<UserEntity> getAllActiveUsersByRole(String role, Authentication auth) {
        return userEntityRepository.findAllByRoleAndEnabled(role, true);
    }

    public UserEntity addUser(String role, UserEntity userEntity) {
        if (!userEntity.getRole().equals(role))
            throw new UnauthorizedUserException("Not authorized to create a new user with this role");
        System.out.println("koll:" + userEntity.toString());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    // Only for seeding purposes - find better solution or remove later!!
    public UserEntity addUser(UserEntity userEntity) {
        System.out.println("koll:" + userEntity.toString());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    // Rewrite this with if checks to see what parameters are supposed to be updated?
    public UserEntity updateUser(String role, Long id, UserEntity userEntity) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setUsername(userEntity.getUsername());
        updatedUserEntity.setFirstname(userEntity.getFirstname());
        updatedUserEntity.setLastname(userEntity.getLastname());
        updatedUserEntity.setPassword(userEntity.getPassword());
        updatedUserEntity.setRole(userEntity.getRole());
        updatedUserEntity.setEnabled(userEntity.getEnabled());
        return userEntityRepository.save(updatedUserEntity);
    }

    public void deleteUser(String role, Long id) {
        userEntityRepository.deleteByRoleAndId(role, id);
    }

    public UserEntity updatePassword(String role, Long id, UserEntity userEntityRecord) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setPassword(userEntityRecord.getPassword());
        return userEntityRepository.save(updatedUserEntity);
    }

    public UserEntity updateRole(String role, Long id, UserEntity userEntity) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setRole(userEntity.getRole());
        return userEntityRepository.save(updatedUserEntity);
    }

}
