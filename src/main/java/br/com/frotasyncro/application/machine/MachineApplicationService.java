package br.com.frotasyncro.application.machine;

import br.com.frotasyncro.controller.machine.model.MachineResponseDTO;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.authentication.AuthenticationService;
import br.com.frotasyncro.domain.employer.EmployerService;
import br.com.frotasyncro.domain.machine.MachineService;
import br.com.frotasyncro.domain.machine.mapper.MachineMapper;
import br.com.frotasyncro.domain.machine.wheeling.TruckTrailerCombinationService;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class MachineApplicationService {

    private final MachineService machineService;
    private final EmployerService employerService;
    private final AuthenticationService authenticationService;
    private final TruckTrailerCombinationService truckTrailerCombinationService;

    public Page<MachineResponseDTO> getMachines(FilteredAndPageableRequestDTO filters) {
        UserEntity userEntity = authenticationService.getLoggedUser();

        if (!userEntity.isAdmin()) {
            EmployerEntity employer = employerService.findByUser(userEntity);

            Optional<TruckTrailerCombinationEntity> truckTrailerCombinationOpt =
                    truckTrailerCombinationService.findActiveByEmployer(employer);

            if (truckTrailerCombinationOpt.isPresent()) {
                TruckTrailerCombinationEntity truckTrailerCombination = truckTrailerCombinationOpt.get();

                if (isNull(filters.getFilters())) {
                    filters.setFilters(new ArrayList<>());
                }


                filters.getFilters().add(
                        new FilterCriteria(
                                "id", FilterCriteriaOperator.IN, truckTrailerCombination.getMachineIds()
                        )
                );

            } else {
                return Page.empty();
            }
        }

        Page<MachineEntity> machineEntityPage = machineService.listMachinePageableAndFiltered(
                filters.getPageRequest(),
                filters.getFilters()
        );

        return machineEntityPage.map(MachineMapper.INSTANCE::toResponseDTO);
    }

}
