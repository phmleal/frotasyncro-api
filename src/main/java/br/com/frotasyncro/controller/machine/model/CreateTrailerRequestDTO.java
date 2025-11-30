package br.com.frotasyncro.controller.machine.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrailerRequestDTO extends CreateMachineRequestDTO {

    @NotNull
    private long mileage;

    @NotNull
    private int axles;

    @NotBlank
    private String chassi;

    @NotBlank
    private String renavam;

    private int currentYearIpva;

}
