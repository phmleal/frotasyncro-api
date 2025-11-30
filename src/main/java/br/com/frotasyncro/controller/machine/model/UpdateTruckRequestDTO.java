package br.com.frotasyncro.controller.machine.model;

import br.com.frotasyncro.domain.machine.enums.MachineStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTruckRequestDTO {

    private String brand;
    private String model;
    private BigDecimal paidAmount;
    private String licensePlate;
    private int modelYear;
    private long mileage;
    private int manufactureYear;
    private int axles;
    private String chassi;
    private String renavam;
    private int currentYearIpva;
    private MachineStatus machineStatus;

}
