package br.com.frotasyncro.controller.employer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateEmployerRequestDTO {

    @NotBlank
    private String socialNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String driverLicense;

    @NotNull
    private LocalDate driverLicenseExpiryDate;

    @NotNull
    private LocalDate medicalExamValidityDate;

    @NotNull
    private LocalDate toxicologicalExamValidityDate;

    @NotNull
    private BigDecimal commissionPercentage;

    @NotNull
    private boolean active;

    private LocalDate birthDate;

    private LocalDate admissionDate;

    @NotNull
    private boolean admin;

}
