package br.com.frotasyncro.domain.authentication;

import br.com.frotasyncro.infrastructure.persistence.authentication.RoleRepository;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleEntity> findAllRoles() {
        return roleRepository.findAll();
    }

    public List<RoleEntity> findBasicRoles() {
        return roleRepository.findBasicRoles();
    }

    public List<RoleEntity> findAdminRoles() {
        return roleRepository.findAdminRoles();
    }
}
