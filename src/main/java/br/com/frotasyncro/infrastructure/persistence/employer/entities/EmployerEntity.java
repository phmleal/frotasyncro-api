package br.com.frotasyncro.infrastructure.persistence.employer.entities;

import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EmployerEntity extends BaseEntity {

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(nullable = false)
    private String socialNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "driver_license", nullable = false)
    private String driverLicense;

    @Column(name = "driver_license_expiry_date", nullable = false)
    private LocalDate driverLicenseExpiryDate;

    @Column(name = "medical_exam_validity_date", nullable = false)
    private LocalDate medicalExamValidityDate;

    @Column(name = "toxicological_exam_validity_date", nullable = false)
    private LocalDate toxicologicalExamValidityDate;

    @Column(name = "commission_percentage", nullable = false)
    private BigDecimal commissionPercentage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true, name = "user_id")
    private UserEntity user;
}
