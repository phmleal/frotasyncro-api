package br.com.frotasyncro.controller.machine.model;

import br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TruckTrailerCombinationResponseDTO {

    private UUID id;

    private TruckResponseDTO truck;

    private SummaryEmployerResponseDTO employer;

    private TrailerResponseDTO trailer;

    private Long initialMileage;

    private Long finalMileage;

    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;

}
