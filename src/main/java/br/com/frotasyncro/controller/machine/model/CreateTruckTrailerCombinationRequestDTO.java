package br.com.frotasyncro.controller.machine.model;

import jakarta.validation.constraints.NotNull;
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
public class CreateTruckTrailerCombinationRequestDTO {

    @NotNull
    private UUID truckId;
    @NotNull
    private UUID trailerId;
    @NotNull
    private UUID employerId;
    @NotNull
    private Long initialMileage;

    private LocalDateTime createdAt;

}
