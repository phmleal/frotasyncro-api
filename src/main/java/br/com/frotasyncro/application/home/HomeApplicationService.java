package br.com.frotasyncro.application.home;

import br.com.frotasyncro.application.contract.delivery.DeliveryApplicationService;
import br.com.frotasyncro.controller.home.model.HomeResponseDTO;
import br.com.frotasyncro.domain.authentication.AuthenticationService;
import br.com.frotasyncro.domain.contract.delivery.DeliveryService;
import br.com.frotasyncro.domain.employer.EmployerService;
import br.com.frotasyncro.domain.machine.wheeling.TruckTrailerCombinationService;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeApplicationService {

    private final AuthenticationService authenticationService;
    private final EmployerService employerService;
    private final DeliveryService deliveryService;
    private final TruckTrailerCombinationService truckTrailerCombinationService;
    private final DeliveryApplicationService deliveryApplicationService;

    public HomeResponseDTO getHomeInfo() {
        UserEntity loggedUser = authenticationService.getLoggedUser();

        EmployerEntity employerEntity = employerService.findByUser(loggedUser);

        HomeResponseDTO homeResponseDTO = new HomeResponseDTO(employerEntity.getFullName());

        if (loggedUser.isAdmin()) {
            buildAdminInfo(homeResponseDTO);
        } else {
            buildEmployerInfo(homeResponseDTO, employerEntity);
        }

        log.info("Successfully retrieved home info for user {}", loggedUser.getId());

        return homeResponseDTO;
    }

    private void buildEmployerInfo(HomeResponseDTO homeResponseDTO,
                                   EmployerEntity employerEntity) {
        deliveryService
                .findLastByEmployer(employerEntity)
                .ifPresent(delivery ->
                        {
                            homeResponseDTO.setOrder(
                                    deliveryApplicationService.getDeliveryDetail(delivery.getId())
                            );

                            homeResponseDTO.setTruckPlate(
                                    delivery.getTruckTrailerCombination().getTruck().getLicensePlate()
                            );

                            homeResponseDTO.setTruckModel(
                                    delivery.getTruckTrailerCombination().getTruck().getModel()
                            );
                        }
                );
    }

    private void buildAdminInfo(HomeResponseDTO homeResponseDTO) {
        var activeEmployersCount = employerService.countActiveNonAdminEmployers();
        var deliveriesInProgressCount = deliveryService.countActiveDeliveries();
        var totalCouplings = truckTrailerCombinationService.countActiveTruckTrailerCombinations();
        var monthlyAmountToReceive = deliveryService.calculateMonthlyAmountToReceive();

        homeResponseDTO.setActiveContracts(deliveriesInProgressCount);
        homeResponseDTO.setActiveEmployers(activeEmployersCount);
        homeResponseDTO.setTotalCouplings(totalCouplings);
        homeResponseDTO.setMonthlyAmountToReceive(monthlyAmountToReceive);
    }

}
