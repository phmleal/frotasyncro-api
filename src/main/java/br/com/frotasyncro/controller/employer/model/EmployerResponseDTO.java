package br.com.frotasyncro.controller.employer.model;

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
public class EmployerResponseDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String socialNumber;
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
