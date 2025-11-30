package br.com.frotasyncro.controller.machine.model;

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
public class UpdateTruckTrailerCombinationRequestDTO {

    private UUID truckId;

    private UUID trailerId;

    private UUID employerId;

    private Long initialMileage;

    private Long finalMileage;

    private LocalDateTime finishedAt;

}

