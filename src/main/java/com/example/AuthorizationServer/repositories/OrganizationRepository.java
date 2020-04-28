package com.example.AuthorizationServer.repositories;

import com.example.AuthorizationServer.bo.entity.Organization;
import com.example.AuthorizationServer.bo.entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface OrganizationRepository extends CrudRepository<Organization, String>, OrganizationRepositoryCustom {

    Optional<Organization> findById(Long id);

    Optional<Organization> findByName(String name);

    List<Organization> findAllByOrderByPathAsc();

    List<Organization> findByPathEndsWith(String id);

    List<Organization> findByPathStartsWith(String id);

    List<Organization> findByPathContains(String id);

    void deleteById(Long id);

    @Query("SELECT path FROM Organization WHERE id =:id")
    String getPath(@Param("id") Long id);

    @Query("SELECT name FROM Organization WHERE id =:id")
    String getName(@Param("id") Long id);

   /* @Query(
            "SELECT e1.ename FROM emp e1, emp e2
            where e2.path LIKE e1.path || '%'
            and e2.name = 'FORD'"
    )
    List<Organization> getParents(@Param("id") Long id);*/
}