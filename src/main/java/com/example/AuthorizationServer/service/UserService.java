package com.example.AuthorizationServer.service;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repository.UserEntityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Service for handling retrieving, saving and updating user entities.
 */
@Repository // Remove?
@Transactional // Remove?
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    // IS ONLY USED BY USERDETAILSSERVICE AND SEED DATABASE!!! - REMOVE LATER?
    public UserEntity getUserByUsername(String username) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findByUsernameAndEnabled(username,true);
        if (!optionalUserEntity.isPresent()) {
            throw new EntityNotFoundException();
        }
        return optionalUserEntity.get();
    }

    public UserEntityDTO getUserByRoleAndUsername(String role, String username) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findByUsernameAndRoleAndEnabled(username, role, true);
        if (!optionalUserEntity.isPresent()) {
            throw new EntityNotFoundException();
        }
        return convertUserEntityToDto(optionalUserEntity.get());
    }

    /**
     * Get a user entity dto from role and id
     * @param role the role of the user entity dto
     * @param id the id of the user entity dto
     * @return the user entity dto
     */
    public UserEntityDTO getUserByRoleAndId(String role, Long id) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUserEntity.isPresent()) {
            throw new EntityNotFoundException();
        }
        return convertUserEntityToDto(optionalUserEntity.get());
    }

    private Optional<UserEntity> getUserById(Long id) {
        return userEntityRepository.findById(id);
    }

    public List<UserEntityDTO> getAllActiveUsersByRole(String role, Authentication auth) {

        List<UserEntity> userEntities = userEntityRepository.findAllByRoleAndEnabled(role, true);
        List<UserEntityDTO> userEntityDTOS = new ArrayList<>();

        for (UserEntity u: userEntities) {
            userEntityDTOS.add(convertUserEntityToDto(u));
        }

        return userEntityDTOS;
    }

    public UserEntityDTO addUser(String role, UserEntityExtendedDTO userEntityDTO) {
        if (!userEntityDTO.getRole().equals(role))
            throw new UnauthorizedUserException("Not authorized to create a new user with this role");
        userEntityDTO.setPassword(new BCryptPasswordEncoder().encode(userEntityDTO.getPassword()));
        if(userEntityDTO.getOrganizations() == null)
            userEntityDTO.setOrganizations(new HashSet<>());
        return convertUserEntityToDto(userEntityRepository.save(convertToEntity(userEntityDTO)));
    }

    public void addUsers(List<UserEntityExtendedDTO> userEntityDTOS) {

        List<UserEntity> userEntities = new ArrayList<>();

        for (UserEntityExtendedDTO u: userEntityDTOS) {
            UserEntity userEntity = convertToEntity(u);
            userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
            userEntities.add(userEntity);
        }
        userEntityRepository.saveAll(userEntities);
    }

    // JUST FOR SEEDING - REMOVE LATER!!
    public UserEntity addUser(UserEntity userEntity) {
        System.out.println("koll:" + userEntity.toString());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    // JUST FOR SEEDING - REMOVE LATER!!
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
        updatedUserEntity.setOrganizations(userEntity.getOrganizations());
        return userEntityRepository.save(updatedUserEntity);
    }

    // Rewrite this with if checks to see what parameters are supposed to be updated?
    public UserEntityDTO updateUser(String role, Long id, UserEntityExtendedDTO userEntityExtendedDTO) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setUsername(userEntityExtendedDTO.getUsername());
        updatedUserEntity.setFirstname(userEntityExtendedDTO.getFirstname());
        updatedUserEntity.setLastname(userEntityExtendedDTO.getLastname());
        updatedUserEntity.setPassword(userEntityExtendedDTO.getPassword());
        updatedUserEntity.setRole(userEntityExtendedDTO.getRole());
        updatedUserEntity.setEnabled(userEntityExtendedDTO.getEnabled());

        Set<Organization> organizations = new HashSet<>();
        for (OrganizationDTO o: userEntityExtendedDTO.getOrganizations()) {
            organizations.add(convertToEntity(o));
        }

        updatedUserEntity.setOrganizations(organizations);
        return convertUserEntityToDto(userEntityRepository.save(updatedUserEntity));
    }

    public void deleteUser(String role, Long id) {
        userEntityRepository.deleteByRoleAndId(role, id);
    }

    public UserEntityDTO updatePassword(String role, Long id, UserEntityExtendedDTO userEntityExtendedDTO) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setPassword(userEntityExtendedDTO.getPassword());
        return convertUserEntityToDto(userEntityRepository.save(updatedUserEntity));
    }

    public UserEntityDTO updateRole(String role, Long id, UserEntityDTO userEntityDTO) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?
        UserEntity updatedUserEntity = optionalUser.get();
        updatedUserEntity.setRole(userEntityDTO.getRole());
        return convertUserEntityToDto(userEntityRepository.save(updatedUserEntity));
    }

    /**
     * Converts a user entity and its organization entity memberships into a user entity dto with organization dto
     * memberships.
     * @param userEntity the user entity to convert
     * @return the corresponding user entity dto
     */
    private UserEntityDTO convertUserEntityToDto(UserEntity userEntity) {
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
    private UserEntityDTO convertToDto(UserEntity userEntity) {
        UserEntityDTO userEntityDTO = modelMapper.map(userEntity, UserEntityDTO.class);

        // Do something else if needed..?

        return userEntityDTO;
    }

    /**
     * Convert user entity dto to user entity
     * @param userEntityDto the user entity dto to convert
     * @return the corresponding user entity
     */
    private UserEntity convertToEntity(UserEntityDTO userEntityDto) throws ParseException {
        UserEntity newUser = modelMapper.map(userEntityDto, UserEntity.class);
        Optional<UserEntity> user = this.getUserById(userEntityDto.getId());
        return user.orElse(null);
    }

    /**
     * Convert organization to organization dto
     * @param organization the organization to convert
     * @return the corresponding user entity dto
     */
    private OrganizationDTO convertToDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);

        // Do something else if needed..?

        return organizationDTO;
    }

    /**
     * Convert user entity dto to user entity
     * @param organizationDto the user entity dto to convert
     * @return the corresponding user entity
     */
    private Organization convertToEntity(OrganizationDTO organizationDto) throws ParseException {
        Organization organization = modelMapper.map(organizationDto, Organization.class);

        // Do something else if needed..?

        return organization;
    }

    /**
     * Convert user entity dto to user entity
     * @param userEntityDto the user entity dto to convert
     * @return the corresponding user entity
     */
    private UserEntity convertToEntity(UserEntityExtendedDTO userEntityDto) throws ParseException {
        UserEntity newUser = modelMapper.map(userEntityDto, UserEntity.class);
        /*Optional<UserEntity> user = this.getUserById(userEntityDto.getId());
        return user.orElse(null);*/
        return newUser;
    }

}
