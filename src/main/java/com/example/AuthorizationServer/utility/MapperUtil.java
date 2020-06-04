package com.example.AuthorizationServer.utility;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.OrganizationTreeNodeDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
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
     * Converts a user entity and its organization entity memberships into a user entity dto with organization dto
     * memberships.
     * @param userEntity the user entity to convert
     * @return the corresponding user entity dto
     */
    public UserEntityDTO convertUserEntityToDto(UserEntity userEntity) {
        Set<OrganizationDTO> orgDtos = new HashSet<>();

        for (Organization o: userEntity.getOrganizations()) {
            orgDtos.add(convertToDto(o));
        }

        UserEntityDTO userEntityDTO = convertToDto(userEntity);
        userEntityDTO.setOrganizations(orgDtos);

        return userEntityDTO;
    }

    /**
     * Convert user entity to user entity dto excluding organization memberships
     * @param userEntity the user entity to convert
     * @return the corresponding user entity dto
     */
    public UserEntityDTO convertToDto(UserEntity userEntity) {
        UserEntityDTO userEntityDTO = modelMapper.map(userEntity, UserEntityDTO.class);

        // Do something else if needed..?

        return userEntityDTO;
    }

    /**
     * Convert organization to organization dto
     * @param organization the organization to convert
     * @return the corresponding user entity dto
     */
    public OrganizationDTO convertToDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);

        // Do something else if needed..?

        return organizationDTO;
    }

    /**
     * Convert user entity dto to user entity
     * @param organizationDto the user entity dto to convert
     * @return the corresponding user entity
     */
    public Organization convertToEntity(OrganizationDTO organizationDto) throws ParseException {
        Organization organization = modelMapper.map(organizationDto, Organization.class);

        // Do something else if needed..?

        return organization;
    }

    /**
     * Convert user entity dto to user entity
     * @param userEntityDto the user entity dto to convert
     * @return the corresponding user entity
     */
    public UserEntity convertToEntity(UserEntityExtendedDTO userEntityDto) throws ParseException {
        UserEntity newUser = modelMapper.map(userEntityDto, UserEntity.class);
        /*Optional<UserEntity> user = this.getUserById(userEntityDto.getId());
        return user.orElse(null);*/
        return newUser;
    }

    /**
     * Converts a organization dtos to a organization tree node dtos.
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
