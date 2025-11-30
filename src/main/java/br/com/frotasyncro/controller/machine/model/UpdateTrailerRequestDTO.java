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
public class UpdateTrailerRequestDTO {

    private String licensePlate;

    private Long mileage;

    private int axles;

    private int modelYear;

    private MachineStatus machineStatus;

    private String brand;

    private String model;

    private BigDecimal paidAmount;

    private String chassi;

    private String renavam;

    private int currentYearIpva;

}
