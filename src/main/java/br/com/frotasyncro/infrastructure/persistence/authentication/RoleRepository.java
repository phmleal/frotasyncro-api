package br.com.frotasyncro.infrastructure.persistence.authentication;

import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query("select r from RoleEntity r where r.authority like '%BASIC%'")
    List<RoleEntity> findBasicRoles();

    @Query("select r from RoleEntity r where r.authority like '%ADMIN%'")
    List<RoleEntity> findAdminRoles();
}
