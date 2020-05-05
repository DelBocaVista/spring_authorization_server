package com.example.AuthorizationServer.services;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Custom implementation of core interface which loads user-specific data.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getUserByUsername(userName);
        GrantedAuthority authority = new SimpleGrantedAuthority(userEntity.getRole());
        Set<Organization> organizations = userEntity.getOrganizations();
        Set<OrganizationDTO> organizationDTOS = new HashSet<>();
        for (Organization o: organizations) {
            organizationDTOS.add(convertToDto(o));
        }
        // The room booking system only uses one authority per user entity but constructor for class
        // org.springframework.security.core.userdetails.User requires Collection.
        return new CustomUserDetails(userEntity.getUsername(), userEntity.getPassword(), Arrays.asList(authority), organizationDTOS);
    }

    private OrganizationDTO convertToDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);

        // Do something else if needed..?

        return organizationDTO;
    }
}