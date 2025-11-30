package br.com.frotasyncro.controller.employer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployerRequestDTO {

    private String socialNumber;

    private String email;

    private String fullName;

    private LocalDate birthDate;

    private LocalDate admissionDate;

    private String driverLicense;

    private LocalDate driverLicenseExpiryDate;

    private LocalDate medicalExamValidityDate;

    private LocalDate toxicologicalExamValidityDate;

    private BigDecimal commissionPercentage;

    private boolean active;

    private boolean admin;

}
