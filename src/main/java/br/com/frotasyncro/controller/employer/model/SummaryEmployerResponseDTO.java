package br.com.frotasyncro.controller.employer.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SummaryEmployerResponseDTO {

    private UUID id;
    private String fullName;
    private BigDecimal commissionPercentage;

    @Nullable
    private UUID truckTrailerCombinationId;

    @Nullable
    private String couplingLicensePlate;

}
