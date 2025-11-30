package br.com.frotasyncro.controller.machine.model;

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
public class CreateMachineRequestDTO {

    private String brand;

    private String model;

    private BigDecimal paidAmount;

    @NotBlank
    private String licensePlate;

    @NotNull
    private Integer modelYear;

    @NotNull
    private int manufactureYear;

}
