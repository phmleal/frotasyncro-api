package br.com.frotasyncro.controller.contract.delivery.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClosingDeliveryRequestDTO {

    @NotNull
    private Long finalMileage;

    @NotNull
    private LocalDate finalDate;

}
