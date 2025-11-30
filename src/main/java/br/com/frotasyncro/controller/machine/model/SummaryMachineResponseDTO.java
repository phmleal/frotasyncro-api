package br.com.frotasyncro.controller.machine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryMachineResponseDTO {

    private UUID id;
    private String licensePlate;

}
