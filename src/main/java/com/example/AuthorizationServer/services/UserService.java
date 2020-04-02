package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository // Remove?
@Transactional // Remove?
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    public UserEntity getUserByUsername(String username) {
        List<UserEntity> test = userEntityRepository.findByUsernameAndEnabled(username, true);
        System.out.println(test.size());
        return test.get(0);
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userEntityRepository.findById(id);
    }

    public List<UserEntity> getAllActiveUsers(Authentication auth) {
        return userEntityRepository.findAllByEnabled(true);
    }

    public UserEntity addUser(UserEntity userEntity) {
        System.out.println("koll:" + userEntity.toString());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    public UserEntity updateUser(Long id, UserEntity userEntity) {
        Optional<UserEntity> optionalUser = userEntityRepository.findById(id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setUsername(userEntity.getUsername());
        updatedUserEntity.setPassword(userEntity.getPassword());
        updatedUserEntity.setRole(userEntity.getRole());
        updatedUserEntity.setEnabled(userEntity.getEnabled());
        return userEntityRepository.save(updatedUserEntity);
    }

    public void deleteUser(Long id) {
        userEntityRepository.deleteById(id);
    }

    public UserEntity updatePassword(Long id, UserEntity userEntityRecord) {
        Optional<UserEntity> optionalUser = userEntityRepository.findById(id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setPassword(userEntityRecord.getPassword());
        return userEntityRepository.save(updatedUserEntity);
    }

    public UserEntity updateRole(Long id, UserEntity userEntity) {
        Optional<UserEntity> optionalUser = userEntityRepository.findById(id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setRole(userEntity.getRole());
        return userEntityRepository.save(updatedUserEntity);
    }

}
