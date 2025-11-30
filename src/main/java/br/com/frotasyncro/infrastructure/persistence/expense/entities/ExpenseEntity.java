package br.com.frotasyncro.infrastructure.persistence.expense.entities;

import br.com.frotasyncro.domain.contract.enums.ExpenseType;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExpenseEntity extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    private Integer quantity;

    private String description;

    private String observation;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "expense_type")
    private ExpenseType expenseType;

}
