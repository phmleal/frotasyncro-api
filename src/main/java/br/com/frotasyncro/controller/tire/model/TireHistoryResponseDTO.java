package br.com.frotasyncro.controller.tire.model;

import br.com.frotasyncro.domain.tire.enums.TireEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TireHistoryResponseDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private TireEventType type;
    private long mileage;
    private String licensePlate;
    private String position;
    private String observation;
    private String workshop;
    private Integer retreadNumber;
}