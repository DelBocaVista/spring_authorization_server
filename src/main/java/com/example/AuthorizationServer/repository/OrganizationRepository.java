package com.example.AuthorizationServer.repository;

import com.example.AuthorizationServer.bo.entity.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Repository for persisting organizations.
 */
@Repository
@Transactional
public interface OrganizationRepository extends CrudRepository<Organization, String> {

    Optional<Organization> findById(Long id);

    Optional<Organization> findByName(String name);

    List<Organization> findAllByOrderByPathAsc();

    List<Organization> findByPathStartsWith(String id);

    List<Organization> findByPathContains(String id);

    List<Organization> findByPathNotContaining(String id);

    List<Organization> findByPathContainsOrderByPathAsc(String id);

    void deleteById(Long id);
}