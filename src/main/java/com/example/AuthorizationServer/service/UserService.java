package com.example.AuthorizationServer.service;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.bo.dto.UserDTO;
import com.example.AuthorizationServer.bo.dto.UserExtendedDTO;
import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.User;
import com.example.AuthorizationServer.repository.OrganizationRepository;
import com.example.AuthorizationServer.repository.UserRepository;
import com.example.AuthorizationServer.utility.MapperUtil;
import com.example.AuthorizationServer.utility.UserEntityDTOComparator;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserRepository userRepository;

    private final OrganizationRepository orgRepository;

    private final OrganizationService orgService;

    private final MapperUtil mapperUtil;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, OrganizationRepository orgRepository, OrganizationService orgService, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.orgService = orgService;
        this.mapperUtil = mapperUtil;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Fetches a user dto from username.
     *
     * @param username the username of the user.
     * @return the user dto.
     */
    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsernameAndEnabled(username,true);
        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException();
        }
        return optionalUser.get();
    }

    /**
     * Fetches a user dto from role and id.
     *
     * @param role the role of the user dto.
     * @param id the id of the user dto.
     * @return the user dto.
     */
    public UserDTO getUserByRoleAndId(String role, Long id) {
        Optional<User> optionalUser = userRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException();
        }
        return mapperUtil.convertUserEntityToDto(optionalUser.get());
    }

    /**
     * Fetches a user from id.
     *
     * @param id the id of the user.
     * @return the optional user.
     */
    private Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Fetches users from role.
     *
     * @param role the role of the users.
     * @return the users found.
     */
    public List<UserDTO> getAllActiveUsersByRole(String role) {

        List<User> users = userRepository.findAllByRoleAndEnabled(role, true);
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User u: users) {
            userDTOS.add(mapperUtil.convertUserEntityToDto(u));
        }

        return userDTOS;
    }

    /**
     * Creates a new user.
     *
     * @param role the allowed role of the new user.
     * @param userDTO the user dto of the new user.
     * @return the user dto representing the new user.
     */
    public UserDTO addUser(String role, UserExtendedDTO userDTO) {
        if (!userDTO.getRole().equals(role))
            throw new UnauthorizedUserException("Not authorized to create a new user with this role");
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        if(userDTO.getOrganizations() == null)
            userDTO.setOrganizations(new HashSet<>());
        return mapperUtil.convertUserEntityToDto(userRepository.save(mapperUtil.convertToEntity(userDTO)));
    }

    /**
     * Creates multiple users.
     *
     * @param userDTOS the list of user dtos representing the new users to be created.
     */
    public void addUsers(List<UserExtendedDTO> userDTOS) {

        List<User> users = new ArrayList<>();

        for (UserExtendedDTO u: userDTOS) {
            User user = mapperUtil.convertToEntity(u);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    /**
     * Updates an existing user.
     *
     * @param role the allowed role of the user.
     * @param id the id of the the user to be updated.
     * @param userDTO the updated user dto version of the user to be updated.
     * @return the user dto representing the updated user.
     */
    public UserDTO updateUser(String role, Long id, UserExtendedDTO userDTO) {

        Optional<User> optionalUser = userRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException(); // ?

        User updatedUser = optionalUser.get();

        if(!userDTO.getUsername().equals(""))
            updatedUser.setUsername(userDTO.getUsername());
        if(!userDTO.getFirstname().equals(""))
            updatedUser.setFirstname(userDTO.getFirstname());
        if(!userDTO.getLastname().equals(""))
            updatedUser.setLastname(userDTO.getLastname());
        if(!userDTO.getPassword().equals(""))
            updatedUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        // System does not allow for changing role of user
        if(userDTO.getEnabled() != null)
            updatedUser.setEnabled(userDTO.getEnabled());
        if(userDTO.getOrganizations() != null) {
            Set<Organization> organizations = new HashSet<>();
            for (OrganizationDTO o : userDTO.getOrganizations()) {
                organizations.add(mapperUtil.convertToEntity(o));
            }
            updatedUser.setOrganizations(organizations);
        }

        return mapperUtil.convertUserEntityToDto(userRepository.save(updatedUser));
    }

    /**
     * Deletes a user.
     *
     * @param role the allowed role of the user to be deleted.
     * @param id the id of the user to be deleted.
     */
    public void deleteUser(String role, Long id) {
        Optional<User> optionalUser = userRepository.findByRoleAndId(role, id);
        if (!optionalUser.isPresent())
            throw new NoSuchElementException();
        userRepository.deleteByRoleAndId(role, id);
    }

    /**
     * Fetches all users of a given organization.
     *
     * @param organizationId the id of the organization to fetch users from.
     * @return the user dtos of the users found.
     */
    public List<UserDTO> getAllUsersByOrganization(Long organizationId) {
        Optional<Organization> optionalOrganization = orgRepository.findById(organizationId);
        if(!optionalOrganization.isPresent())
            throw new NoSuchElementException();
        Organization organization = optionalOrganization.get();
        List<User> result = userRepository.findAllByOrganizationsContainsAndRoleAndEnabled(organization,
                "USER", true);
        List<UserDTO> resultDTOs = new ArrayList<>();
        for (User u: result) {
            resultDTOs.add(mapperUtil.convertUserEntityToDto(u));
        }
        return resultDTOs;
    }

    /**
     * Fetches unique users belonging to a given root organization or one of its sub organizations. Results are
     * sorted by username and chosen by given limit and offset values.
     *
     * @param organizationId the id of the root organization.
     * @param limit the upper limit of how many users to retrieve.
     * @param offset the offset for where in the ordered listing to start retrieving users.
     * @return the dtos of the users found.
     */
    public List<UserDTO> getAllUsersByRootOrganization(Long organizationId, int limit, int offset) {
        List<UserDTO> all = getAllUsersByRootOrganization(organizationId);

        return new ArrayList<>(
                all.subList(
                        Math.min(all.size(), offset),
                        Math.min(all.size(), offset + limit)
                )
        );
    }

    /**
     * Fetches unique users belonging to a given root organization or one of its sub organizations. Results are
     * sorted by username and chosen by given limit.
     * @param organizationId the id of the root organization.
     * @param limit
     * @return
     */
    public List<UserDTO> getAllUsersByRootOrganization(Long organizationId, int limit) {
        return getAllUsersByRootOrganization(organizationId, limit, 0);
    }

    /**
     * Fetches all unique users belonging to a given root organization or one of its sub organizations. Results
     * are sorted by username.
     *
     * @param organizationId the id of the root organization.
     * @return the list of user dtos representing the users found.
     */
    public List<UserDTO> getAllUsersByRootOrganization(Long organizationId) {

        if(!orgService.isRootOrganization(organizationId))
            throw new NoSuchElementException();

        Optional<Organization> optionalOrganization = orgRepository.findById(organizationId);

        if(!optionalOrganization.isPresent())
            throw new NoSuchElementException();

        Organization organization = optionalOrganization.get();

        List<OrganizationDTO> subOrganizations = orgService.getAllChildrenOfOrganization(organization.getId());

        Set<UserDTO> users = new HashSet<>();

        // Add root organization members
        List<UserDTO> rootMembers = this.getAllUsersByOrganization(organization.getId());
        users.addAll(rootMembers);

        for (OrganizationDTO o: subOrganizations) {
            Optional<Organization> optional = orgRepository.findById(o.getId());
            if(!optional.isPresent())
                throw new NoSuchElementException();
            List<UserDTO> members = this.getAllUsersByOrganization(o.getId());
            users.addAll(members);
        }

        List<UserDTO> result = new ArrayList<>(users);

        // Sort by username
        result.sort(UserEntityDTOComparator.INSTANCE);

        return result;
    }
}
