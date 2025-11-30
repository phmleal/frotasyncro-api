package br.com.frotasyncro.core.specification;

import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Specification para filtrar apenas Employers cujos usuários NÃO possuem role de Admin.
 */
public class NonAdminEmployerSpecification implements Specification<EmployerEntity> {

    @Override
    public Predicate toPredicate(Root<EmployerEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Subquery para encontrar todos os usuários com role admin
        Subquery<UUID> adminUsersSubquery = query.subquery(UUID.class);
        Root<UserEntity> userRoot = adminUsersSubquery.from(UserEntity.class);
        Join<UserEntity, RoleEntity> roleJoin = userRoot.join("roles");

        adminUsersSubquery.select(userRoot.get("id"))
                .where(cb.like(cb.lower(roleJoin.get("authority")), "%admin%"));

        // Retorna predicado que exclui employers cujos usuários estão na subquery
        return cb.not(root.get("user").get("id").in(adminUsersSubquery));
    }
}

