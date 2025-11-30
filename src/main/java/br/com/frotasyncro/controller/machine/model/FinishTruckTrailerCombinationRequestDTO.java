package br.com.frotasyncro.controller.machine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinishTruckTrailerCombinationRequestDTO {

    private Long finalMileage;

    private LocalDateTime finishedAt;

}

