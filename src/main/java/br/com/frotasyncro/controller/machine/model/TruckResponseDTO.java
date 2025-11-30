package br.com.frotasyncro.controller.machine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TruckResponseDTO extends MachineResponseDTO {

    private long mileage;
    
    private int axles;

    private String chassi;

    private String renavam;

    private int currentYearIpva;

}
