package br.com.frotasyncro.application.employer;

import br.com.frotasyncro.application.authentication.AuthenticationApplicationService;
import br.com.frotasyncro.controller.authentication.model.CreateUserRequestDTO;
import br.com.frotasyncro.controller.employer.model.CreateEmployerRequestDTO;
import br.com.frotasyncro.controller.employer.model.EmployerResponseDTO;
import br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO;
import br.com.frotasyncro.controller.employer.model.UpdateEmployerRequestDTO;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.domain.authentication.RoleService;
import br.com.frotasyncro.domain.authentication.UserService;
import br.com.frotasyncro.domain.authentication.enums.UserStatus;
import br.com.frotasyncro.domain.employer.EmployerService;
import br.com.frotasyncro.domain.employer.mapper.EmployerMapper;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployerApplicationService {

    private final EmployerService employerService;
    private final AuthenticationApplicationService authenticationApplicationService;
    private final UserService userService;
    private final RoleService roleService;

    @Transactional
    public EmployerResponseDTO createEmployer(CreateEmployerRequestDTO requestDTO) {
        UserEntity userEntity = authenticationApplicationService
                .createUserEntity(new CreateUserRequestDTO(requestDTO.getSocialNumber(), requestDTO.getEmail()), requestDTO.isAdmin());

        EmployerEntity employerEntity = EmployerMapper.INSTANCE.toEntity(requestDTO, userEntity);

        employerService.saveEmployer(employerEntity);

        log.info("Employer {} successfully created", employerEntity.getId());

        userService.sendWelcomeEmailToUser(userEntity);

        return EmployerMapper.INSTANCE.toResponseDTO(employerEntity);
    }

    public EmployerResponseDTO getEmployerDetail(UUID employerId) {
        EmployerEntity employerEntity = employerService.findById(employerId);

        return EmployerMapper.INSTANCE.toResponseDTO(employerEntity);
    }

    public void updateEmployer(UUID id, UpdateEmployerRequestDTO updateEmployerRequestDTO) {
        EmployerEntity employerEntity = employerService.findById(id);

        EmployerMapper.INSTANCE.updateEntity(employerEntity, updateEmployerRequestDTO);

        if (isNotBlank(updateEmployerRequestDTO.getSocialNumber()) && employerEntity.getSocialNumber()
                .compareTo(updateEmployerRequestDTO.getSocialNumber()) != 0) {
            String socialNumber = updateEmployerRequestDTO.getSocialNumber();

            employerEntity.setSocialNumber(socialNumber);
            employerEntity.getUser().setUsername(socialNumber);
        }

        if (employerEntity.getUser().getStatus().isActive() != updateEmployerRequestDTO.isActive()) {
            employerEntity.getUser().setStatus(updateEmployerRequestDTO.isActive() ? UserStatus.ACTIVE : UserStatus.INACTIVE);
        }

        if (updateEmployerRequestDTO.isAdmin() != employerEntity.getUser().isAdmin()) {
            Set<RoleEntity> roleEntities =
                    new HashSet<>(updateEmployerRequestDTO.isAdmin() ? roleService.findAdminRoles() : roleService.findBasicRoles());

            employerEntity.getUser().setRoles(
                    roleEntities
            );
        }

        employerService.saveEmployer(employerEntity);
    }

    public void deleteEmployer(UUID id) {
        employerService.deleteEmployer(id);

        log.info("Employer {} successfully deleted", id);
    }

    public Page<EmployerResponseDTO> getEmployers(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        Page<EmployerEntity> employerEntityPage = employerService.listEmployerPageableAndFiltered(
                filteredAndPageableRequestDTO.getPageRequest(),
                filteredAndPageableRequestDTO.getFilters()
        );

        return employerEntityPage.map(EmployerMapper.INSTANCE::toResponseDTO);
    }

    public List<SummaryEmployerResponseDTO> getSummaryEmployer() {
        return employerService.getSummaryEmployer();
    }
}
