package com.example.AuthorizationServer.service;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityExtendedDTO;
import com.example.AuthorizationServer.bo.dto.UserEntityDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import com.example.AuthorizationServer.repository.UserEntityRepository;
import com.example.AuthorizationServer.utility.UserEntityDTOComparator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se), Erik Wikzén (wikzen@kth.se)
 *
 * Service for handling retrieving, saving and updating user entities.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Fetches a user entity dto from username.
     *
     * @param username the username of the user entity.
     * @return the user entity dto.
     */
    public UserEntity getUserByUsername(String username) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findByUsernameAndEnabled(username,true);
        if (!optionalUserEntity.isPresent()) {
            throw new EntityNotFoundException();
        }
        return optionalUserEntity.get();
    }

    /**
     * Fetches a user entity dto from role and id.
     *
     * @param role the role of the user entity dto.
     * @param id the id of the user entity dto.
     * @return the user entity dto.
     */
    public UserEntityDTO getUserByRoleAndId(String role, Long id) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUserEntity.isPresent()) {
            throw new EntityNotFoundException();
        }
        return convertUserEntityToDto(optionalUserEntity.get());
    }

    /**
     * Fetches a user entity from id.
     *
     * @param id the id of the user entity.
     * @return the optional user entity.
     */
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

    /**
     * Create a new user entity.
     *
     * @param role the allowed role of the new user entity.
     * @param userEntityDTO the user entity dto of the new user entity.
     * @return the user entity dto representing the new user.
     */
    public UserEntityDTO addUser(String role, UserEntityExtendedDTO userEntityDTO) {
        if (!userEntityDTO.getRole().equals(role))
            throw new UnauthorizedUserException("Not authorized to create a new user with this role");
        userEntityDTO.setPassword(new BCryptPasswordEncoder().encode(userEntityDTO.getPassword()));
        if(userEntityDTO.getOrganizations() == null)
            userEntityDTO.setOrganizations(new HashSet<>());
        return convertUserEntityToDto(userEntityRepository.save(convertToEntity(userEntityDTO)));
    }

    /**
     * Create multiple user entities.
     *
     * @param userEntityDTOS the list of user entity dtos representing the new user entities to be created.
     */
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

    /**
     * Update an existing user entity.
     *
     * @param role the allowed role of the user entity.
     * @param id the id of the the user entity to be updated.
     * @param userEntityDTO the updated user entity dto version of the user entity to be updated.
     * @return the user entity dto representing the updated user entity.
     */
    public UserEntityDTO updateUser(String role, Long id, UserEntityExtendedDTO userEntityDTO) {

        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?

        UserEntity updatedUserEntity = optionalUser.get();

        if(!userEntityDTO.getUsername().equals(""))
            updatedUserEntity.setUsername(userEntityDTO.getUsername());
        if(!userEntityDTO.getFirstname().equals(""))
            updatedUserEntity.setFirstname(userEntityDTO.getFirstname());
        if(!userEntityDTO.getLastname().equals(""))
            updatedUserEntity.setLastname(userEntityDTO.getLastname());
        if(!userEntityDTO.getPassword().equals(""))
            updatedUserEntity.setPassword(new BCryptPasswordEncoder().encode(userEntityDTO.getPassword()));
        // System does not allow for changing role of user
        if(userEntityDTO.getEnabled() != null)
            updatedUserEntity.setEnabled(userEntityDTO.getEnabled());
        if(userEntityDTO.getOrganizations() != null) {
            Set<Organization> organizations = new HashSet<>();
            for (OrganizationDTO o : userEntityDTO.getOrganizations()) {
                organizations.add(convertToEntity(o));
            }
            updatedUserEntity.setOrganizations(organizations);
        }

        return convertUserEntityToDto(userEntityRepository.save(updatedUserEntity));
    }

    /**
     * Deletes a user entity.
     *
     * @param role the allowed role of the user entity to be deleted.
     * @param id the id of the user entity to be deleted.
     */
    public void deleteUser(String role, Long id) {
        Optional<UserEntity> optionalUser = userEntityRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException();
        userEntityRepository.deleteByRoleAndId(role, id);
    }

    /**
     * Fetch all user entities of a given organization.
     *
     * @param organizationId the id of the organization to fetch user entities from.
     * @return the user entity dtos of the user entities found.
     */
    public List<UserEntityDTO> getAllUsersByOrganization(Long organizationId) {
        Optional<Organization> optionalOrganization = orgRepository.findById(organizationId);
        if(!optionalOrganization.isPresent())
            throw new NoSuchElementException();
        Organization organization = optionalOrganization.get();
        List<UserEntity> result = userEntityRepository.findAllByOrganizationsContainsAndRoleAndEnabled(organization,
                "USER", true);
        List<UserEntityDTO> resultDTOs = new ArrayList<>();
        for (UserEntity u: result) {
            resultDTOs.add(convertUserEntityToDto(u));
        }
        return resultDTOs;
    }

    /**
     * Fetch unique user entities belonging to a given root organization or one of its sub organizations. Results are
     * sorted by username and chosen by given limit and offset values.
     *
     * @param organizationId the id of the root organization.
     * @param limit
     * @param offset
     * @return
     */
    public List<UserEntityDTO> getAllUsersByRootOrganization(Long organizationId, int limit, int offset) {
        List<UserEntityDTO> all = getAllUsersByRootOrganization(organizationId);

        return new ArrayList<>(
                all.subList(
                        Math.min(all.size(), offset),
                        Math.min(all.size(), offset + limit)
                )
        );
    }

    /**
     * Fetch unique user entities belonging to a given root organization or one of its sub organizations. Results are
     * sorted by username and chosen by given limit.
     * @param organizationId the id of the root organization.
     * @param limit
     * @return
     */
    public List<UserEntityDTO> getAllUsersByRootOrganization(Long organizationId, int limit) {
        return getAllUsersByRootOrganization(organizationId, limit, 0);
    }

    /**
     * Fetches all unique user entities belonging to a given root organization or one of its sub organizations. Results
     * are sorted by username.
     *
     * @param organizationId the id of the root organization.
     * @return the list of user entity dtos representing the user entities found.
     */
    public List<UserEntityDTO> getAllUsersByRootOrganization(Long organizationId) {

        if(!orgService.isRootOrganization(organizationId))
            throw new NoSuchElementException();

        Optional<Organization> optionalOrganization = orgRepository.findById(organizationId);

        if(!optionalOrganization.isPresent())
            throw new NoSuchElementException();

        Organization organization = optionalOrganization.get();

        List<OrganizationDTO> subOrganizations = orgService.getAllChildrenOfOrganization(organization.getId());

        Set<UserEntityDTO> users = new HashSet<>();

        // Add root organization members
        List<UserEntityDTO> rootMembers = this.getAllUsersByOrganization(organization.getId());
        users.addAll(rootMembers);

        for (OrganizationDTO o: subOrganizations) {
            Optional<Organization> optional = orgRepository.findById(o.getId());
            if(!optional.isPresent())
                throw new NoSuchElementException();
            List<UserEntityDTO> members = this.getAllUsersByOrganization(o.getId());
            users.addAll(members);
        }

        List<UserEntityDTO> result = new ArrayList<>(users);

        // Sort by username
        result.sort(UserEntityDTOComparator.INSTANCE);

        return result;
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
