package br.com.frotasyncro.controller.contract.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateContractRequestDTO {

    @NotNull
    private BigDecimal contractValue;

    @NotBlank
    private String referenceCode;

}
