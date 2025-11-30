package br.com.frotasyncro.controller.contract.model;

import br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponseDTO {

    private UUID id;

    private String referenceCode;

    private BigDecimal commission;

    private BigDecimal contractValue;

    private BigDecimal advanceValue;

    private LocalDateTime createdAt;

    private SummaryEmployerResponseDTO employer;

    private BigDecimal totalExpenses;

    private BigDecimal remainingBalance;

}
