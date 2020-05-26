package com.example.AuthorizationServer.security;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.service.UserService;
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
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Custom implementation of core interface UserDetailsService which loads user-specific data.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    /**
     * {@inheritDoc}
     *
     * Override adds custom information to user details.
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getUserByUsername(userName);
        GrantedAuthority authority = new SimpleGrantedAuthority(userEntity.getRole());

        Set<Organization> organizations = userEntity.getOrganizations();
        Set<OrganizationDTO> organizationDTOS = new HashSet<>();
        for (Organization o: organizations) {
            organizationDTOS.add(convertToDto(o));
        }

        return new CustomUserDetails(userEntity.getUsername(), userEntity.getPassword(), Arrays.asList(authority),
                userEntity.getId(), organizationDTOS);
    }

    private OrganizationDTO convertToDto(Organization organization) {
        return modelMapper.map(organization, OrganizationDTO.class);
    }
}