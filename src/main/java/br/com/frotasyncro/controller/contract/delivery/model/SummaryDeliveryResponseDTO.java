package br.com.frotasyncro.controller.contract.delivery.model;

import br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO;
import br.com.frotasyncro.controller.machine.model.SummaryTruckTrailerCombinationResponseDTO;
import br.com.frotasyncro.domain.contract.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDeliveryResponseDTO {

    private UUID id;

    private String referenceCode;

    private BigDecimal commission;

    private BigDecimal contractValue;

    private SummaryEmployerResponseDTO employer;

    private String destiny;

    private String origin;

    private LocalDate startDate;

    private LocalDate finalDate;

    private Long initialMileage;

    private Long finalMileage;

    private DeliveryStatus deliveryStatus;

    private SummaryTruckTrailerCombinationResponseDTO truckTrailerCombination;

}
