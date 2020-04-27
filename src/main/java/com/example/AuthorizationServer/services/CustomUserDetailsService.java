package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getUserByUsername(userName);
        GrantedAuthority authority = new SimpleGrantedAuthority(userEntity.getRole());

        // The room booking system only uses one authority per user entity but constructor for class
        // org.springframework.security.core.userdetails.User requires Collection.
        return new User(userEntity.getUsername(), userEntity.getPassword(), Arrays.asList(authority));
    }
}