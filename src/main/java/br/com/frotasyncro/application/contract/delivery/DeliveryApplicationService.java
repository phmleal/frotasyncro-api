package br.com.frotasyncro.application.contract.delivery;

import br.com.frotasyncro.controller.contract.delivery.model.*;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.core.specification.enums.FilterCriteriaOperator;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.authentication.AuthenticationService;
import br.com.frotasyncro.domain.contract.delivery.DeliveryService;
import br.com.frotasyncro.domain.contract.delivery.mapper.DeliveryMapper;
import br.com.frotasyncro.domain.machine.wheeling.TrailerService;
import br.com.frotasyncro.domain.machine.wheeling.TruckService;
import br.com.frotasyncro.domain.machine.wheeling.TruckTrailerCombinationService;
import br.com.frotasyncro.domain.tire.TireService;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.contract.entities.DeliveryEntity;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TrailerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryApplicationService {

    private final DeliveryService deliveryService;
    private final TruckTrailerCombinationService truckTrailerCombinationService;
    private final AuthenticationService authenticationService;
    private final TireService tireService;
    private final TruckService truckService;
    private final TrailerService trailerService;

    private static void setRemainingBalance(DeliveryResponseDTO deliveryResponseDTO, DeliveryEntity deliveryEntity) {
        deliveryResponseDTO.setRemainingBalance(
                isNull(deliveryEntity.getExpenses()) ?
                        deliveryResponseDTO.getAdvanceValue() :
                        deliveryResponseDTO.getAdvanceValue()
                                .subtract(deliveryResponseDTO.getCommission())
                                .subtract(
                                        deliveryEntity.getExpenses().stream()
                                                .map(ExpenseEntity::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                )
        );
    }

    private static void setTotalExpenses(DeliveryResponseDTO deliveryResponseDTO, DeliveryEntity deliveryEntity) {
        deliveryResponseDTO.setTotalExpenses(
                isNull(deliveryEntity.getExpenses()) ? BigDecimal.ZERO :
                        deliveryEntity.getExpenses().stream()
                                .map(ExpenseEntity::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private void addMileageToLinks(DeliveryEntity deliveryEntity) {
        long totalMileageTraveled =
                deliveryEntity.getFinalMileage() - deliveryEntity.getInitialMileage();

        TruckEntity truck = deliveryEntity.getTruckTrailerCombination().getTruck();

        tireService.addMileageToTireByMachineOwner(truck, totalMileageTraveled);

        truckService.addMileageToTruck(truck, totalMileageTraveled);

        log.info("Added {} km to tires of truck: {}", totalMileageTraveled, truck.getLicensePlate());

        TrailerEntity trailer = deliveryEntity.getTruckTrailerCombination().getTrailer();

        tireService.addMileageToTireByMachineOwner(trailer, totalMileageTraveled);

        trailerService.addMileageToTrailer(trailer, totalMileageTraveled);

        log.info("Added {} km to tires of trailer: {}", totalMileageTraveled, trailer.getLicensePlate());
    }

    public DeliveryResponseDTO createDelivery(CreateDeliveryRequestDTO createDeliveryRequestDTO) {
        TruckTrailerCombinationEntity truckTrailerCombination =
                truckTrailerCombinationService.findById(createDeliveryRequestDTO.getTruckTrailerCombinationId());

        DeliveryEntity deliveryEntity =
                DeliveryMapper.INSTANCE.toEntity(createDeliveryRequestDTO,
                        truckTrailerCombination);

        deliveryEntity.setCommission(
                calculateComission(deliveryEntity.getAdvanceValue(),
                        truckTrailerCombination.getEmployer().getCommissionPercentage())
        );

        deliveryService.saveDelivery(deliveryEntity);

        log.info("Create delivery: {}", deliveryEntity);

        return DeliveryMapper.INSTANCE.toResponseDTO(deliveryEntity);
    }

    private BigDecimal calculateComission(@NotNull BigDecimal contractValue, BigDecimal commissionPercentage) {

        if (commissionPercentage == null) {
            return BigDecimal.ZERO;
        }
        return contractValue.multiply(commissionPercentage).divide(BigDecimal.valueOf(100));
    }

    public void startDelivery(UUID deliveryId,
                              StartDeliveryRequestDTO startDeliveryRequestDTO) {
        DeliveryEntity deliveryEntity = deliveryService.findById(deliveryId);

        deliveryService.startDelivery(deliveryEntity,
                startDeliveryRequestDTO.getStartDate(),
                startDeliveryRequestDTO.getInitialMileage());

        log.info("Delivery started: {}", deliveryEntity.getId());
    }

    public void closingDelivery(UUID deliveryId, ClosingDeliveryRequestDTO closingDeliveryRequestDTO) {
        DeliveryEntity deliveryEntity = deliveryService.findById(deliveryId);

        deliveryService.closingDelivery(deliveryEntity,
                closingDeliveryRequestDTO.getFinalDate(), closingDeliveryRequestDTO.getFinalMileage());

        log.info("Delivery in closing: {}", deliveryEntity.getId());
    }

    @Transactional
    public void finishDelivery(UUID deliveryId) {
        DeliveryEntity deliveryEntity = deliveryService.findById(deliveryId);

        deliveryService.finishDelivery(deliveryEntity);

        log.info("Delivery in finished: {}", deliveryEntity.getId());

        addMileageToLinks(deliveryEntity);
    }

    public Page<SummaryDeliveryResponseDTO> getDeliveries(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        changeVisibilityToEmployer(filteredAndPageableRequestDTO);

        Page<DeliveryEntity> deliveryEntityPage =
                deliveryService.listDeliveriesPageableAndFiltered(
                        filteredAndPageableRequestDTO.getPageRequest(),
                        filteredAndPageableRequestDTO.getFilters()
                );

        return deliveryEntityPage.map(DeliveryMapper.INSTANCE::toSummaryResponseDTO);
    }

    private void changeVisibilityToEmployer(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        UserEntity userEntity = authenticationService.getLoggedUser();

        if (!userEntity.isAdmin()) {
            if (filteredAndPageableRequestDTO.getFilters() == null) {
                filteredAndPageableRequestDTO.setFilters(new ArrayList<>());
            }

            filteredAndPageableRequestDTO.getFilters().add(
                    new FilterCriteria("employer.user.id",
                            FilterCriteriaOperator.EQUAL, userEntity.getId())
            );
        }
    }

    public DeliveryResponseDTO getDeliveryDetail(UUID id) {
        DeliveryEntity deliveryEntity = deliveryService.findById(id);

        DeliveryResponseDTO deliveryResponseDTO =
                DeliveryMapper.INSTANCE.toResponseDTO(deliveryEntity);

        setTotalExpenses(deliveryResponseDTO, deliveryEntity);

        setRemainingBalance(deliveryResponseDTO, deliveryEntity);

        return deliveryResponseDTO;
    }
}
