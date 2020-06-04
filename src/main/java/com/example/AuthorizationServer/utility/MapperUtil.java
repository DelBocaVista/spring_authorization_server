package com.example.AuthorizationServer.utility;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.bo.dto.UserDTO;
import com.example.AuthorizationServer.bo.dto.UserExtendedDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Utility for mapping and converting between entity and dto classes.
 */
@Service
public class MapperUtil {

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Converts a user and its organization memberships into a user dto with organization dto memberships.
     *
     * @param user the user to convert
     * @return the corresponding user dto
     */
    public UserDTO convertUserEntityToDto(User user) {
        Set<OrganizationDTO> orgDtos = new HashSet<>();

        for (Organization o: user.getOrganizations()) {
            orgDtos.add(convertToDto(o));
        }

        UserDTO userDTO = convertToDto(user);
        userDTO.setOrganizations(orgDtos);

        return userDTO;
    }

    /**
     * Convert user to user dto excluding organization memberships.
     *
     * @param user the user to convert.
     * @return the corresponding user dto.
     */
    public UserDTO convertToDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    /**
     * Convert organization to organization dto.
     *
     * @param organization the organization to convert.
     * @return the corresponding organization dto.
     */
    public OrganizationDTO convertToDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);
        return organizationDTO;
    }

    /**
     * Convert user dto to user.
     *
     * @param organizationDto the user dto to convert.
     * @return the corresponding user.
     */
    public Organization convertToEntity(OrganizationDTO organizationDto) throws ParseException {
        Organization organization = modelMapper.map(organizationDto, Organization.class);
        return organization;
    }

    /**
     * Convert extended user dto to user.
     *
     * @param userDto the user dto to convert.
     * @return the corresponding user.
     */
    public User convertToEntity(UserExtendedDTO userDto) throws ParseException {
        User newUser = modelMapper.map(userDto, User.class);
        return newUser;
    }

    /**
     * Converts organization dtos to organization tree node dtos.
     *
     * @param organizations the organizations to be converted.
     * @return the corresponding organization tree node dtos.
     */
    public List<OrganizationTreeNodeDTO> convertToTreeNodeDtos(List<OrganizationDTO> organizations) {
        List<OrganizationTreeNodeDTO> nodes = new ArrayList<>();

        for (OrganizationDTO o: organizations) {
            OrganizationTreeNodeDTO n = new OrganizationTreeNodeDTO();
            n.setId(o.getId());
            n.setName(o.getName());
            n.setPath(o.getPath());
            n.setEnabled(o.getEnabled());
            nodes.add(n);
        }
        return nodes;
    }
}
