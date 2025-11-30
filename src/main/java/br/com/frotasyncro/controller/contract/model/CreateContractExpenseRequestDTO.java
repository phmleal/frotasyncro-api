package br.com.frotasyncro.controller.contract.model;

import br.com.frotasyncro.domain.contract.enums.ExpenseType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateContractExpenseRequestDTO {
    @NotNull
    private ExpenseType expenseType;
    @NotNull
    private LocalDate date;
    @NotNull
    private Double amount;
    private String description;
    private String observation;
    private BigDecimal unitCost;
    private Integer quantity;
}
