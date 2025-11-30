package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireCondition;
import br.com.frotasyncro.domain.tire.enums.TireStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TireResponseDTO {

    private UUID id;
    private LocalDate purchaseDate;
    private int manufactureYear;
    private String manufacturer;
    private String fireCode;
    private String observation;
    private TireCondition tireCondition;
    private TireStatus tireStatus;
    private BigDecimal price;
    private Long mileage;
    private LocalDateTime createdAt;
    private UUID machineId;
    private String licensePlate;
    private String equipmentPosition;

    private String locationStatus;
    private LocalDateTime lastMaintenance;
    private LocalDateTime nextMaintenance;
    private String supplier;
    private String retreadSerialNumber;
    private String retreadCount;

    private List<TireHistoryResponseDTO> history;

}
