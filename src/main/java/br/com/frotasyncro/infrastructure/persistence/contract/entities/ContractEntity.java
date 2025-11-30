package br.com.frotasyncro.infrastructure.persistence.contract.entities;

import br.com.frotasyncro.domain.contract.enums.ContractType;
import br.com.frotasyncro.infrastructure.persistence.attachment.entities.AttachmentEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.expense.entities.ExpenseEntity;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "contracts")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ContractEntity extends BaseEntity {

    @Column(nullable = false, name = "ref_code", unique = true)
    private String referenceCode;

    @Column(nullable = false)
    private BigDecimal commission;

    @Column(nullable = false, name = "contract_value")
    private BigDecimal contractValue;

    @Column(name = "advance_value", nullable = false)
    private BigDecimal advanceValue;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "contracts_expenses",
            joinColumns = @JoinColumn(name = "contract_id", referencedColumnName = "id", table = "contracts"),
            inverseJoinColumns = @JoinColumn(name = "expense_id", referencedColumnName = "id", table = "expenses"))
    private Set<ExpenseEntity> expenses;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "contracts_attachments",
            joinColumns = @JoinColumn(name = "contract_id", referencedColumnName = "id", table = "contracts"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id", referencedColumnName = "id", table = "attachments"))
    private Set<AttachmentEntity> attachments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerEntity employer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType type;

}
