package br.com.frotasyncro.controller.contract.model;

import br.com.frotasyncro.domain.contract.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateContractExpenseResponseDTO {
    private UUID id;
    private ExpenseType expenseType;
    private LocalDate date;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private String observation;
    private BigDecimal unitCost;
    private Integer quantity;
}
